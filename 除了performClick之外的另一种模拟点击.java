    
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
