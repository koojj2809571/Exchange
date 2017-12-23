package com.opp.android.exchange._deleteClass;

/**
 * Created by OPP on 2017/8/29.
 */

public class CountryListFragment{
//    private static final String TAG = "CountryListFragment";
//    private RecyclerView mCountryRecyclerView;
//    private CountryAdapter mAdapter;
//    private List<Country> mCountries;
//    private CountryStore mCountryStore;
//    private RefreshableView mRefreshableView;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        CountryListBinding binding = DataBindingUtil.inflate(inflater,R.layout.country_list,container,false);
//        mCountryRecyclerView = binding.countryRecyclerView;
//        mCountryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        updateUI();
//        return binding.getRoot();
//    }
//
//    private class CountryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        private ListItemCountryBinding mBinding;
//        private Country mCountry;
//
//        public CountryHolder(ListItemCountryBinding binding) {
//            super(binding.getRoot());
//            mBinding = binding;
//            mBinding.getRoot().setOnClickListener(this);
//            mBinding.setViewModel(new CountryViewModel(mCountryStore));
//        }
//
//        public void bindCountry(Country country){
//            mCountry = country;
//            mBinding.getViewModel().setCountry(mCountry);
//            mBinding.executePendingBindings();
//        }
//
//        @Override
//        public void onClick(View view) {
//            returnResult(mCountry.getCurrency(),mCountry.getRate());
//        }
//    }
//
//    private class CountryAdapter extends RecyclerView.Adapter<CountryHolder>{
//        public CountryAdapter(List<Country> countries){
//            mCountries = countries;
//        }
//
//        @Override
//        public CountryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//            ListItemCountryBinding binding= DataBindingUtil.inflate(layoutInflater,R.layout.list_item_country,parent,false);
//            return new CountryHolder(binding);
//        }
//
//        @Override
//        public void onBindViewHolder(CountryHolder holder, int position) {
//            Country country = mCountries.get(position);
//            holder.bindCountry(country);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mCountries.size();
//        }
//
//
//    }
//
//    private void updateUI(){
//        mCountryStore = CountryStore.get(getActivity());
//        mCountries = mCountryStore.getCountries();
//        mAdapter = new CountryAdapter(mCountries);
//        mCountryRecyclerView.setAdapter(mAdapter);
//        mRefreshableView.setOnRefreshListener(new PullToRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new UpdateTask().execute();
//                mRefreshableView.finishRefreshing();
//            }
//        },0);
//    }
//
//    public void returnResult(String currency,double rate){
//        Intent intent = new Intent();
//        intent.putExtra("currency_return",currency);
//        intent.putExtra("rate_return",rate);
//        getActivity().setResult(Activity.RESULT_OK,intent);
//        getActivity().finish();
//    }
//
//    class UpdateTask extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            try {
//                CountryRates countryRates = new UpdateRateUtils().fetchCountry();
//                CountryStore countryStore = CountryStore.get(getActivity());
//                List<Country> countries = countryStore.loadRates(getActivity(), countryRates);
//            }catch (Exception e){
//                Log.d(TAG, "有问题！！！！：" + e.toString());
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            if (result){
//                Toast.makeText(getActivity(),"更新成功",Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(getActivity(),"更新失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
