package kr.ssc.front.ui.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.ssc.front.R;

public class AttendanceAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<Attendance> list = new ArrayList<Attendance>();

    public AttendanceAdapter(Context context, int layout, ArrayList<Attendance> list) {
        this.context = context;
        this.layout = layout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(list != null) return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if(view == null) {
            view = inflater.inflate(layout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvAttendanceDate = view.findViewById(R.id.text_attendance_date);
            viewHolder.tvAttendanceState = view.findViewById(R.id.text_attendance_state);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        Attendance attendance = list.get(position);

        viewHolder.tvAttendanceDate.setText(attendance.getAttendance_time());
        viewHolder.tvAttendanceState.setText(attendance.getAttendance_state());

        return view;
    }

    public void setList(ArrayList<Attendance> list) {
        this.list = list;
    }

    static class ViewHolder {
        public TextView tvAttendanceDate = null;
        public TextView tvAttendanceState = null;
    }

}
