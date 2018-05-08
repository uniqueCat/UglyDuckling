package msf.uglyduckling.ui.express;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseFragment;
import msf.uglyduckling.bean.BaseResponse;
import msf.uglyduckling.bean.PackageBean;
import msf.uglyduckling.net.Api;
import msf.uglyduckling.net.RetrofitUtil;
import msf.uglyduckling.net.service.PackageService;
import msf.uglyduckling.realm.RealmHistoryHelper;
import msf.uglyduckling.realm.RealmPackageHelper;
import msf.uglyduckling.utils.LogUtils;
import msf.uglyduckling.utils.NetworkUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/4/13.
 * List的hashCode由list元素觉得，一样元素的list hashCode一样 和list是new的 还是 list = list 无关，
 * 这里一样的元素指的是对象地址是一样的的，而不是对象表面的一样，是那种这个对象在别的地方改变了，这个list中的对象也改变这种
 */

public class ExpressFragment extends BaseFragment implements BottomNavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyView)
    LinearLayout emptyView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;


    private String showType = TYPE_ALL;
    private static final String TYPE_ALL = "all";
    private static final String TYPE_TRANSPORT = "transport";
    private static final String TYPE_DELIVERED = "delivered";

    public static final int CODE_ADD_PACKAGE = 0x25;
    public static final int CODE_PACKAGE_DETAILS = 0x26;

    private static final int ACTION_SWITCH_VIEW = 0x31;
    private static final int ACTION_UPDATE_VIEW = 0x32;

    //ArrayList 内容一样，hashCode也一样？ 然后addAll到另一个集合有一个会失败？？ 神奇的问题  unbelievable
    private ArrayList<PackageBean> transportPackages = new ArrayList<>();   //运输中的快递
    private ArrayList<PackageBean> deliveredPackages = new ArrayList<>();   //已送达的快递

    private PackagesAdapter adapter;

    private PackageBean selectedPackage;  //选中的包裹，复制跟分享用到里面的数据，选择是在holder里面
    private PackageBean removePackageBean;

    private Disposable disposable;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_express;
    }

    @Override
    protected void viewCreate() {
        setHasOptionsMenu(true);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        localLoadingPackages();

        // ItemTouchHelper helps to handle the drag or swipe action.
        // In our app, we do nothing but return a false value
        // means the item does not support drag action.
        //可以实现列表item上下拖动左右滑动操作，传入不同值表示支持哪些操作                                        这里支持左右滑动，不支持上下拖动
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    //先把数据拷贝一个对象出来保存，已供撤销操作
                    removePackageBean = adapter.list.get(viewHolder.getLayoutPosition()).clone();
                    //从数据看删除
                    RealmPackageHelper.getInstance().deletedPackage(removePackageBean.getMailNo());
                    //更新数据，刷新视图
                    localLoadingPackages();
                    //提醒已删除，显示撤销按钮
                    Snackbar.make(fab, removePackageBean.getName() + getString(R.string.package_removed_msg), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (removePackageBean != null) {
                                        RealmPackageHelper.getInstance().increaseOrUpdate(removePackageBean);
                                        localLoadingPackages();
                                    }
                                }
                            }).show();
                } catch (CloneNotSupportedException e) {
                    LogUtils.e(" 捕获异常:" + e.toString());
                    showSnack(fab, "操作失败了！！你可以提醒开发者改进");
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // The callback when releasing the view, hide the icons.
                // ViewHolder's ItemView is the default view to be operated.
                // Here we call getDefaultUIUtil's function clearView to pass
                // specified view.
                getDefaultUIUtil().clearView(((PackagesAdapter.PackageViewHolder) viewHolder).layoutMain);
                //其实不隐藏也行，会被内容视图挡住的，严谨嘛，   背景要一个也行可以啊
                ((PackagesAdapter.PackageViewHolder) viewHolder).textViewRemove.setVisibility(View.GONE);
                ((PackagesAdapter.PackageViewHolder) viewHolder).imageViewRemove.setVisibility(View.GONE);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                // The callback when ViewHolder's status of drag or swipe action changed.
                if (viewHolder != null) {
                    // ViewHolder's ItemView is the default view to be operated.
                    getDefaultUIUtil().onSelected(((PackagesAdapter.PackageViewHolder) viewHolder).layoutMain);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //改变滑动的view 滑动不是整个itemView 而是itemView的内容view，itemView里面的背景删除view不滑动
                getDefaultUIUtil().onDraw(c, recyclerView, ((PackagesAdapter.PackageViewHolder) viewHolder).layoutMain, dX, dY, actionState, isCurrentlyActive);
                //滑动显示删除背景 左滑右滑显示不同删除背景
                if (dX > 0) {
                    //left滑
                    ((PackagesAdapter.PackageViewHolder) viewHolder).wrapperView.setBackgroundResource(R.color.deep_orange);
                    ((PackagesAdapter.PackageViewHolder) viewHolder).imageViewRemove.setVisibility(View.VISIBLE);
                    ((PackagesAdapter.PackageViewHolder) viewHolder).textViewRemove.setVisibility(View.GONE);
                }
                if (dX < 0) {
                    //right滑
                    ((PackagesAdapter.PackageViewHolder) viewHolder).wrapperView.setBackgroundResource(R.color.deep_orange);
                    ((PackagesAdapter.PackageViewHolder) viewHolder).imageViewRemove.setVisibility(View.GONE);
                    ((PackagesAdapter.PackageViewHolder) viewHolder).textViewRemove.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //改变滑动的view 滑动不是整个itemView 而是itemView的内容view，itemView里面的背景删除view不滑动
                getDefaultUIUtil().onDrawOver(c, recyclerView, ((PackagesAdapter.PackageViewHolder) viewHolder).layoutMain, dX, dY, actionState, isCurrentlyActive);
            }
        });
        //绑定RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.packages_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (disposable != null && disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mark_all_read:
                for (PackageBean transportPackage : transportPackages) {
                    RealmPackageHelper.getInstance().updatePackageReadState(transportPackage.getMailNo(), true);
                }
                for (PackageBean deliveredPackage : deliveredPackages) {
                    RealmPackageHelper.getInstance().updatePackageReadState(deliveredPackage.getMailNo(), true);
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                break;
            case R.id.action_search:
                jumpActivity(SearchActivity.class);
                break;
        }
        return true;
    }

    @Override
    //给某个view设置setOnCreateContextMenuListener后，长按回调onCreateContextMenu，里面有个menu参数，往menu里面add 子项实现列表菜单，列表item点击回调到这里。
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_readable:
                if (selectedPackage.isRead())
                    RealmPackageHelper.getInstance().updatePackageReadState(selectedPackage.getMailNo(), false);
                else
                    RealmPackageHelper.getInstance().updatePackageReadState(selectedPackage.getMailNo(), true);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                break;
            case R.id.action_copy_code:
                copyPackageNumber();
                break;
            case R.id.action_share:
                shareTo();
                break;
        }
        return true;
    }

    //复制运单号
    private void copyPackageNumber() {
        ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text", selectedPackage.getMailNo());
        manager.setPrimaryClip(data);
        showSnack(fab, getString(R.string.package_number_copied));
    }

    //分享包裹
    private void shareTo() {
        String shareData = selectedPackage.getName()
                + "\n("
                + selectedPackage.getMailNo()
                + " "
                + selectedPackage.getExpTextName()
                + ")\n"
                + getString(R.string.latest_status);
        if (selectedPackage.getData() != null && !selectedPackage.getData().isEmpty()) {
            shareData = shareData + selectedPackage.getData().get(0).getContext() + selectedPackage.getData().get(0).getTime();
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

    //显示包裹类型切换
    private void switchPackages() {
        switch (showType) {
            case TYPE_ALL:
                //这里为什么不把list拿出去做一个放所有包裹的集合？
                //发现当放所有包裹的list内容和放已送达或运输中list一样时(hashCode也一样，虽然都是直接new出来的)，切换时在adapter的list addAll尽然返回false失败！然后出现各种玄学问题。
                ArrayList<PackageBean> list = new ArrayList<>();
                list.addAll(deliveredPackages);
                list.addAll(transportPackages);
                updateView(list);
                break;
            case TYPE_DELIVERED:
                updateView(deliveredPackages);
                break;
            case TYPE_TRANSPORT:
                updateView(transportPackages);
                break;
        }
    }

    private void updateView(ArrayList<PackageBean> packages) {
        if (packages.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new PackagesAdapter(packages);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(packages);
            }
        }

    }

    //从数据库查询包裹
    private void localLoadingPackages() {
        List<PackageBean> allPackages = RealmPackageHelper.getInstance().queryAll();
        LogUtils.e("  SOURCE_LOCAL allPackages size:" + allPackages.size());
        refreshDate(allPackages);
    }

    //刷新数据更新视图
    private void refreshDate(List<PackageBean> allPackages) {
        transportPackages.clear();
        deliveredPackages.clear();
        for (PackageBean packageBean : allPackages) {
            LogUtils.e("  package status:" + packageBean.getStatus());
            if (packageBean.getStatus() == 4) {
                deliveredPackages.add(packageBean);
            } else {
                transportPackages.add(packageBean);
            }
        }
        eventHandler(ACTION_SWITCH_VIEW, bottomNavigationView.getSelectedItemId());
    }

    //从网络查询某类型包裹
    private void obtainPackages() {
        switch (showType) {
            case TYPE_ALL:
                final List<PackageBean> packageBeans = RealmPackageHelper.getInstance().queryAll();
                requestPackages(packageBeans);
                break;
            case TYPE_DELIVERED:
                requestPackages(deliveredPackages);
                break;
            case TYPE_TRANSPORT:
                requestPackages(transportPackages);
                break;
        }
    }

    //发射一个集合的包裹 主线程拿到运单号，io线程请求数据，转成list 转到主线程 保存到数据库 判断结果 通知从数据库更新刷新视图
    private void requestPackages(final List<PackageBean> packageBeans) {
        disposable = Observable
                //接收一个集合发射多次， 主线程
                .fromIterable(packageBeans)
                //传入包裹对象，拿到运单号String返回
                .flatMap(new Function<PackageBean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final PackageBean pack) throws Exception {
                        if (TextUtils.isEmpty(pack.getMailNo()))
                            //如果订单号为空，跳过此条请求
                            return Observable.empty();
                        //发射在io线程中getMailNo报非法状态，所以多加了个flatMap，先在主线程中getMailNo，再转到io线程请求网络
                        return Observable.just(pack.getMailNo());
                    }
                })
                //切换到io线程
                .observeOn(Schedulers.io())
                //传入运单号，请求网络，得到BaseResponse<PackageBean>结果返回
                .flatMap(new Function<String, ObservableSource<BaseResponse<PackageBean>>>() {
                    @Override
                    public ObservableSource<BaseResponse<PackageBean>> apply(String no) throws Exception {
                        return RetrofitUtil.getRetrofit().create(PackageService.class)
                                .queryPackage(Api.SHOWAPI_APPID, Api.SHOWAPI_SIGN, "auto", no);
                    }
                })
                //传入BaseResponse<PackageBean> 判断code是否成功 决定返回包裹PackageBean还是跳过此条
                .flatMap(new Function<BaseResponse<PackageBean>, ObservableSource<PackageBean>>() {
                    @Override
                    public ObservableSource<PackageBean> apply(BaseResponse<PackageBean> packageBeanBaseResponse) throws Exception {
                        if (packageBeanBaseResponse.showapi_res_code != 0)
                            return Observable.empty();
                        return Observable.just(packageBeanBaseResponse.showapi_res_body);

                    }
                })
                //fromIterable 传入list逐条发送处理，到这里等到所有条处理完转为list(List<PackageBean>)，而不是PackageBean继续往下处理
                .toList()
                //转为可观察者  相当于Observable.just(list<PackageBean>)
                .toObservable()
                //切换到主线程
                .observeOn(AndroidSchedulers.mainThread())
                //对数据进行一次处理
                .doOnNext(new Consumer<List<PackageBean>>() {
                    @Override
                    public void accept(List<PackageBean> packageBeans) throws Exception {
                        for (PackageBean packageBean : packageBeans) {
                            //把一些后期自定义的属性复制过去一起存
                            PackageBean pb = RealmPackageHelper.getInstance().query(packageBean.getMailNo());
                            packageBean.setName(pb.getName());
                            packageBean.setColorAvatar(pb.getColorAvatar());
                            if (pb.isRead()) {
                                LogUtils.e("packageBean isNull " + (packageBean.getData() == null));
                                LogUtils.e("  pb isNull " + (pb.getData() == null));
                                LogUtils.e("  pb.size():" + pb.getData().size());
                                LogUtils.e("  local isRead");
                                //判断是不是没有新的物流
                                if ((packageBean.getData() == null && pb.getData() == null)
                                        || (packageBean.getData() == null && pb.getData() != null && pb.getData().size() == 0)
                                        || (packageBean.getData() != null && pb.getData() != null && packageBean.getData().size() == pb.getData().size())) {
                                    packageBean.setRead(true);  //true为已读
                                }
                            }
                        }
                        //存入数据库
                        RealmPackageHelper.getInstance().increasesOrUpdate(packageBeans);
                    }
                })
                //最终消费者，通知刷新视图，
                .subscribeWith(new DisposableObserver<List<PackageBean>>() {
                    @Override
                    public void onNext(List<PackageBean> packages) {
                        LogUtils.e(" onNext :" + packages);
                        int i = packageBeans.size() - packages.size();
                        if (i > 0) {
                            showSnack(fab, i + "条数据刷新失败！");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(" onError :" + e.toString());
                        if (NetworkUtil.netIsAvailable(getContext()))
                            showSnack(fab, "数据刷新失败！");
                        else
                            showSnack(fab, getString(R.string.network_error));
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e(" onComplete");
                        localLoadingPackages();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void eventHandler(int action, int navId) {
        switch (navId) {
            case R.id.nav_all:
                showType = TYPE_ALL;
                break;
            case R.id.nav_on_the_way:
                showType = TYPE_TRANSPORT;
                break;
            case R.id.nav_delivered:
                showType = TYPE_DELIVERED;
                break;
        }
        if (action == ACTION_SWITCH_VIEW) {
            switchPackages();
        } else if (action == ACTION_UPDATE_VIEW) {
            obtainPackages();
        }
    }


    @OnClick(R.id.fab)
    public void onViewClicked() {
        startActivityForResult(new Intent(getContext(), AddPackageActivity.class), CODE_ADD_PACKAGE);
    }

    @Override   //底部导航栏切换或查询回来刷新视图
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        eventHandler(ACTION_SWITCH_VIEW, item.getItemId());
        return true;
    }

    @Override
    public void onRefresh() {
        eventHandler(ACTION_UPDATE_VIEW, bottomNavigationView.getSelectedItemId());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)    //添加包裹成功  或者查看时修改了名字或删除了  都用从本地重新获取数据刷新
            localLoadingPackages();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //数据库关闭，防止内存泄漏
        RealmHistoryHelper.getInstance().close();
    }

    class PackagesAdapter extends RecyclerView.Adapter<PackagesAdapter.PackageViewHolder> {

        private ArrayList<PackageBean> list;

        private String[] packageStatus;

        public PackagesAdapter(ArrayList<PackageBean> list) {
            this.list = list;
            packageStatus = getResources().getStringArray(R.array.package_status);
        }

        @Override
        public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PackageViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_package, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(PackageViewHolder holder, int position) {
            PackageBean item = list.get(position);

            if (item.getData() != null && item.getData().size() > 0) {
                int state = item.getStatus() < 0 ? packageStatus.length - 1 : item.getStatus();
                holder.textViewStatus.setText(packageStatus[state] + "-" + item.getData().get(0).getContext());
                holder.textViewTime.setText(item.getUpdateStr());
            } else {
                holder.textViewTime.setText("");
                holder.textViewStatus.setText(R.string.get_status_error);
            }

            if (item.isRead()) {
                holder.textViewPackageName.setTypeface(null, Typeface.NORMAL);
                holder.textViewTime.setTypeface(null, Typeface.NORMAL);
                holder.textViewStatus.setTypeface(null, Typeface.NORMAL);
            } else {
                holder.textViewPackageName.setTypeface(null, Typeface.BOLD);
                holder.textViewTime.setTypeface(null, Typeface.BOLD);
                holder.textViewStatus.setTypeface(null, Typeface.BOLD);
            }
            holder.textViewPackageName.setText(item.getName());
            if (item.getName() == null)
                return;
            holder.textViewAvatar.setText(item.getName().substring(0, 1));
            holder.circleImageViewAvatar.setImageResource(item.getColorAvatar());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void updateData(List<PackageBean> list) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        class PackageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {


            AppCompatTextView textViewPackageName;
            AppCompatTextView textViewTime;
            AppCompatTextView textViewStatus;
            AppCompatTextView textViewAvatar;
            AppCompatTextView textViewRemove;
            ImageView imageViewRemove;
            CircleImageView circleImageViewAvatar;
            LinearLayout layoutMain;
            View wrapperView;

            public PackageViewHolder(View itemView) {
                super(itemView);
                textViewPackageName = itemView.findViewById(R.id.textViewPackageName);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
                textViewTime = itemView.findViewById(R.id.textViewTime);
                textViewAvatar = itemView.findViewById(R.id.textViewAvatar);
                textViewRemove = itemView.findViewById(R.id.textViewRemove);
                imageViewRemove = itemView.findViewById(R.id.imageViewRemove);
                circleImageViewAvatar = itemView.findViewById(R.id.circleImageView);
                layoutMain = itemView.findViewById(R.id.layoutPackageItemMain);
                wrapperView = itemView.findViewById(R.id.layoutPackageItem);

                itemView.setOnClickListener(this);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (menu != null) {
                    selectedPackage = list.get(getLayoutPosition());
                    PackageBean pack = list.get(getLayoutPosition());
                    menu.setHeaderTitle(pack.getName());
                    if (selectedPackage.isRead())
                        menu.add(Menu.NONE, R.id.action_set_readable, 0, R.string.set_unread);
                    else
                        menu.add(Menu.NONE, R.id.action_set_readable, 0, R.string.set_read);
                    menu.add(Menu.NONE, R.id.action_copy_code, 0, R.string.copy_code);
                    menu.add(Menu.NONE, R.id.action_share, 0, R.string.share);
                }
            }

            @Override
            //给itemView设置点击事件，再通过holder getLayoutPosition得到点击第几个item，getLayoutPosition()得到得是最新的，可能在recyclerView进行移动删除插入操作后，getPosition和数据的list下标可能不对应
            public void onClick(View v) {
                selectedPackage = list.get(getLayoutPosition());
                startActivityForResult(new Intent(getContext(), PackageDetailsActivity.class).putExtra(PackageBean.PRIMARY_KEY, selectedPackage.getMailNo()), CODE_PACKAGE_DETAILS);
            }
        }
    }
}
