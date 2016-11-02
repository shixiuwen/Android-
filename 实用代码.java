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
      public void onClickRD() {
            emulateClick(mBt_konw);
        }

    private void emulateClick(View view) {
        view.dispatchTouchEvent(MotionEvent
                .obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN, view.getLeft() + 5, view.getTop() + 5, 0));
        view.dispatchTouchEvent(MotionEvent
                .obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP, view.getLeft() + 5, view.getTop() + 5, 0));
    }

    /**
     * 以下代码解决ScrollView中EditText导致自动滚动问题
     * */
      scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setOnTouchListener((v, event) -> {
            v.requestFocusFromTouch();
            return false;
        });
    
