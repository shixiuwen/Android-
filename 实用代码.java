/**
     * 收起软键盘
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mFragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(mFragmentActivity.getWindow().getDecorView().getWindowToken(), 0x0);
        }
    }
    
    /**
     * 除了performClick之外的另一种模拟点击
     * */
    
