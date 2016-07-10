package com.lj.library.fragment.permissionmanage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.Toaster;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liujie_gyh on 16/7/7.
 */
public class PermissionTestFragment extends BaseFragment {

    private static final int REQUEST_CONTACT = 1;

    @Bind(R.id.phone_num_tv)
    TextView mPhoneNumTV;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.permission_test_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    @OnClick(R.id.pick_contact_btn)
    public void pickContact(View view) {
        //ContextCompat.checkSelfPermission()在targetSdkVersion<23时
        // 即使是Android6.0的手机也会一直返回PackageManager.permission.PERMISSION_GRANTED
        if (PermissionChecker.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                Toaster.showShort(mActivity, "需要此权限来选取联系人信息");
            }
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
        } else {
            pushToContactsAppForPickOne();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pushToContactsAppForPickOne();
            }
        }
    }

    private void pushToContactsAppForPickOne() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        if (resultCode == mActivity.RESULT_OK && requestCode == REQUEST_CONTACT) {
            ContentResolver reContentResolver = mActivity.getContentResolver();
            Uri contactData = data.getData();
            Cursor cursor = mActivity.managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phone.moveToNext()) {
                String userNum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mPhoneNumTV.setText(username + " : " + userNum);
            }
        }
    }
}
