package com.jalen.flowlayout;

import com.jalen.flowlayout.FlowLayout.OnCheckedChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements OnCheckedChangeListener {

 	private FlowLayout mFlowLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mFlowLayout = (FlowLayout) findViewById(R.id.id_flowlayout);
		mFlowLayout.setOnCheckedChangeListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(FlowLayout group, int checkedId) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "第 " + checkedId + " 个tab被点击了", Toast.LENGTH_SHORT).show();
	}
}
