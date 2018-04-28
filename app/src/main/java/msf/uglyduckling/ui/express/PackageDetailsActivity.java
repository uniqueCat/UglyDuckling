package msf.uglyduckling.ui.express;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseActivity;
import msf.uglyduckling.bean.PackageBean;
import msf.uglyduckling.bean.PackageStateBean;
import msf.uglyduckling.net.BeanCallback;
import msf.uglyduckling.net.model.ExpressModel;
import msf.uglyduckling.realm.RealmPackageHelper;
import msf.uglyduckling.utils.NetworkUtil;
import msf.uglyduckling.utils.ScreenUtils;
import msf.uglyduckling.widget.NotifyDialog;
import msf.uglyduckling.widget.Timeline;

/**
 * Created by Administrator on 2018/4/25.
 */

public class PackageDetailsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    private boolean isUpdate;
    private PackageBean aPackage;
    private List<PackageStateBean> list;
    private PackageDetailsAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_package_details;
    }

    @Override
    protected void init() {
        aPackage = RealmPackageHelper.getInstance().query(getIntent().getStringExtra(PackageBean.PRIMARY_KEY));
        if (!aPackage.isRead()) {
            RealmPackageHelper.getInstance().updatePackageReadState(aPackage.getMailNo(), true);
            isUpdate = true;
        }
        list = aPackage.getData();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        toolbarLayout.setBackgroundResource(R.drawable.banner_background_delivered);
        adapter = new PackageDetailsAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.package_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_set_readable:
                RealmPackageHelper.getInstance().updatePackageReadState(aPackage.getMailNo(), false);
                isUpdate = true;
                onBackPressed();
                break;
            case R.id.action_copy_code:
                copyPackageNumber();
                break;
            case R.id.action_share:
                shareTo();
                break;
            case R.id.action_delete:
                RealmPackageHelper.getInstance().deletedPackage(aPackage.getMailNo());
                isUpdate = true;
                onBackPressed();
                break;
        }
        return true;
    }

    //复制运单号
    private void copyPackageNumber() {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text", aPackage.getMailNo());
        manager.setPrimaryClip(data);
        showSnack(fab, getString(R.string.package_number_copied));
    }

    //分享包裹
    private void shareTo() {
        String shareData = aPackage.getName()
                + "\n("
                + aPackage.getMailNo()
                + " "
                + aPackage.getExpTextName()
                + ")\n"
                + getString(R.string.latest_status);
        if (aPackage.getData() != null && !aPackage.getData().isEmpty()) {
            shareData = shareData + aPackage.getData().get(0).getContext() + aPackage.getData().get(0).getTime();
        } else {
            shareData = shareData + getString(R.string.get_status_error);
        }
        try {
            Intent intent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareData);
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        } catch (Exception e) {
            showSnack(fab, getString(R.string.something_wrong));
        }
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        FrameLayout layout = new FrameLayout(this);
        layout.setPadding(ScreenUtils.dip2px(this, 20),
                ScreenUtils.dip2px(this, 10),
                ScreenUtils.dip2px(this, 20),
                ScreenUtils.dip2px(this, 10));
        final TextInputEditText editText = new TextInputEditText(this);
        editText.setLines(1);
        editText.setText(aPackage.getName());
        editText.setSelection(aPackage.getName().length());
        layout.addView(editText);
        new NotifyDialog()
                .setTitle(getString(R.string.edit_name))
                .setView(layout)
                .setOnCheckListener(new NotifyDialog.OnCheckListener() {
                    @Override
                    public void isCheck(boolean check) {
                        if (check) {
                            String newName = editText.getText().toString();
                            if (TextUtils.isEmpty(newName)) {
                                showSnack(fab, getString(R.string.input_empty));
                                return;
                            }
                            updateAndNotify(newName);
                        }
                    }
                }).show(this);
    }

    private void updateAndNotify(String newName) {
        RealmPackageHelper.getInstance().updatePackageName(aPackage.getMailNo(), newName);
        adapter.notifyItemChanged(0);   //数据库那边改了，这边的aPackage也发生改变，因为是同一个对象，只需通知适配器去刷新就好了
        isUpdate = true;
    }

    @Override
    public void onRefresh() {
        ExpressModel.queryPackage(aPackage.getExpSpellName(), aPackage.getMailNo(), new BeanCallback<PackageBean>() {
            @Override
            public void onSuccess(PackageBean data) {
                if (data.getData() != null) {
                    if (aPackage.getData() == null) {
                        isUpdate = true;
                        showSnack(fab, getString(R.string.package_update_success_newstatus));
                    } else if (data.getData().size() > aPackage.getData().size()) {
                        isUpdate = true;
                        showSnack(fab, getString(R.string.package_update_success_newstatus));
                    }
                } else {
                    showSnack(fab, getString(R.string.package_update_success_not_newstatus));
                }
                data.setName(aPackage.getName());
                data.setColorAvatar(aPackage.getColorAvatar());
                data.setRead(true);
                RealmPackageHelper.getInstance().increaseOrUpdate(data);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                if (NetworkUtil.netIsAvailable(PackageDetailsActivity.this)) {
                    showSnack(fab, msg);
                } else {
                    showSnack(fab, getString(R.string.network_error));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isUpdate) {
            setResult(RESULT_OK);
        }
        finish();

    }

    class PackageDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_HEADER = 0x00;
        public static final int TYPE_NORMAL = 0x01;
        public static final int TYPE_START = 0x02;
        public static final int TYPE_FINISH = 0x03;
        public static final int TYPE_SINGLE = 0x04;


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER)
                return new HeaderViewHolder(LayoutInflater.from(PackageDetailsActivity.this).inflate(R.layout.item_details_header, parent, false));
            return new PackageStatusViewHolder(LayoutInflater.from(PackageDetailsActivity.this).inflate(R.layout.item_package_status, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                HeaderViewHolder vh = (HeaderViewHolder) holder;
                vh.textViewCompany.setText(aPackage.getExpTextName());
                vh.textViewName.setText(aPackage.getName());
                vh.textViewNumber.setText(aPackage.getMailNo());
            } else {
                PackageStateBean item = list.get(position - 1);
                PackageStatusViewHolder viewHolder = (PackageStatusViewHolder) holder;
                if (getItemViewType(position) == TYPE_SINGLE) {
                    viewHolder.timeLine.setStartLine(null);
                    viewHolder.timeLine.setFinishLine(null);
                } else if (getItemViewType(position) == TYPE_START) {
                    viewHolder.timeLine.setStartLine(null);
                } else if (getItemViewType(position) == TYPE_FINISH) {
                    viewHolder.timeLine.setFinishLine(null);
                }
                viewHolder.textViewTime.setText(item.getTime());
                viewHolder.textViewLocation.setText(item.getContext());
                String context = item.getContext();
                String phone = getPhone(context);    //自己从运送信息里截取出手机号，可能没有
                if (TextUtils.isEmpty(phone)) {
                    viewHolder.contactCard.setVisibility(View.GONE);
                } else {
                    viewHolder.contactCard.setVisibility(View.VISIBLE);
                    viewHolder.textViewPhone.setText(phone);
                }
            }
        }

        private String getPhone(String context) {
            String phone = "";
            int statIndex = context.indexOf("1");
            int counter = 0;
            if (statIndex > -1) {
                for (int i = statIndex; i < context.toCharArray().length; i++) {
                    char c = context.charAt(i);
                    if (Character.isDigit(c)) { //是否是数字
                        if (counter == 11) {
                            int newStarting = context.indexOf("1", i);
                            if (newStarting > -1) {
                                i = newStarting;
                            } else {
                                phone = "";
                                break;
                            }
                        }
                        counter++;
                        phone = phone.concat(String.valueOf(c));
                    } else if (counter == 11) {  //1 开头到不是数字的字符 被认为是一个手机号
                        break;
                    } else {
                        int newStarting = context.indexOf("1", i);
                        if (newStarting > -1)
                            i = newStarting;
                        else {
                            phone = "";
                            break;
                        }
                    }
                }
            }
            return phone;
        }

        @Override
        public int getItemCount() {
            return aPackage.getData().size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            } else if (position == 1 && position == list.size()) {
                return TYPE_SINGLE;
            } else if (position == 1) {
                return TYPE_START;
            } else if (position == list.size()) {
                return TYPE_FINISH;
            }
            return TYPE_NORMAL;
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {

            AppCompatTextView textViewCompany;
            AppCompatTextView textViewNumber;
            AppCompatTextView textViewName;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                textViewCompany = itemView.findViewById(R.id.textViewCompany);
                textViewNumber = itemView.findViewById(R.id.textViewPackageNumber);
                textViewName = itemView.findViewById(R.id.textViewName);

                textViewCompany.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }

        class PackageStatusViewHolder extends RecyclerView.ViewHolder {

            private AppCompatTextView textViewLocation;
            private AppCompatTextView textViewTime;
            private Timeline timeLine;
            private CardView contactCard;
            private AppCompatTextView textViewPhone;

            public PackageStatusViewHolder(View itemView) {
                super(itemView);
                textViewLocation = itemView.findViewById(R.id.textViewStatus);
                textViewTime = itemView.findViewById(R.id.textViewTime);
                timeLine = itemView.findViewById(R.id.timeLine);
                contactCard = itemView.findViewById(R.id.contactCard);
                textViewPhone = itemView.findViewById(R.id.textViewPhone);

                contactCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = textViewPhone.getText().toString();
                        if (uri.length() > 0) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + uri));
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }
}
