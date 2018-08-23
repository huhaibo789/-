package presenter;


import android.util.Log;

import com.example.chengduhome.R;

import view.Mainview;

/**
 * Created by Administrator on 2017/8/31.
 */

public class MainPresenterImpl implements MainPresenter{
    private Mainview mMainView;

    public MainPresenterImpl(Mainview mainView) {
        this.mMainView = mainView;
    }
    public void switchNavigation(int id) {
        switch (id) {
            case R.id.nav_camera:
                mMainView.switch2News();
                break;
            case R.id.nav_help:
                Log.i("jfnfrr", "switchNavigation: "+id);

                mMainView.switch2Images();
                break;
            case R.id.nav_gally:
                mMainView.switch2Weather();
                break;
         /*   case R.id.nav_slideshow:
                mMainView.switch2Weather();
                break;
            case R.id.nav_manage:
                mMainView.switch2About();
                break;*/
            default:
                mMainView.switch2News();
                break;
        }
    }
}
