package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import entity.SingleStatusInfo;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainFragmentAdapter extends CommonAdapter<SingleStatusInfo> {

    public MainFragmentAdapter(List<SingleStatusInfo> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
