package com.drawer.test.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.drawer.test.R;
import com.drawer.test.adapters.GridViewImageAdapter;
import com.drawer.test.utils.AppConstant;
import com.drawer.test.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {
    @Bind (R.id.grid_view)
    protected GridView gridView;
    protected Utils utils;
    protected int columnWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        ButterKnife.bind(this);
        utils = new Utils(this);
        InitilizeGridLayout();
        gridView.setAdapter(new GridViewImageAdapter(GalleryActivity.this, utils.getFilePaths(),
                columnWidth));
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());
        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1)
                * padding)) / AppConstant.NUM_OF_COLUMNS);
        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:{
                finish();
            }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
