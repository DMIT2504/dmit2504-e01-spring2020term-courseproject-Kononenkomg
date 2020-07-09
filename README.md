# Getting started with Stock Market App


## Prerequisite

- Ensure that [Android Studio](https://developer.android.com/studio) is atleast installed

## How To Use

> To clone and run this application, you'll need Git installed on your computer. Clone the repository and open it in your Android Studio

> Start the project

> On the first Activity you can see 3 preinstalled companies.

> In the top righ corner you can see + icon which is goign to lead you to another Activity where we can Add/Delete companies you want to follow.

> On Add/Delete Activity you can add stock by entering stock name to editText and cickink + button. App checks if stock exost automatically. By selecting stock and clicking - you can remove stock.

> You can go back to Main Activity by clicking standart Android back button 

> On the Main Activity you can click on the company name and it will lead you to Activity with detailed information.

- Stock Name
- Country of the company
- Exchange market
- Industry
- Full Name of the company
- Currency
- Web Url of the company

## Api to use

> [Finnhub](https://finnhub.io/) is a public API which provides data about stock market, companies and other usefull data.

> [Documentation](https://finnhub.io/docs/api) for API Finnhub provides.

# Code to get quotes for companie price

    Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://finnhub.io")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
    Connector connector = retrofit.create(Connector.class);

    Call<String> getCall = connector.StockMetrics("api/v1/quote?symbol=" + stockName + "&token=YourToken");
    getCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(final Call<String> call, final Response<String> response) {
                        String responseBody = response.body();
                        JSONObject jsonObjet = null;
                        try {
                            jsonObjet = new JSONObject(responseBody);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            currentPrice = Float.parseFloat(jsonObjet.getString("c"));
                            closingPrice = Float.parseFloat(jsonObjet.getString("pc"));

## Code for checking the price and assigning color

    String name = stock.getText().toString();
                                Cursor cursorWithPrices = mDbConnection.getStockWithPrices(name);
                                if (cursorWithPrices.moveToFirst()){
                                    Float current = cursorWithPrices.getFloat(2);
                                    Float closing = cursorWithPrices.getFloat(3);
                                    if (closing > current){
                                        arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                                        price.setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        arrow.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                                        price.setTextColor(getResources().getColor(R.color.colorGreen));
                                    }
                                }