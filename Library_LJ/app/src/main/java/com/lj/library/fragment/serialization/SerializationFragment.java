package com.lj.library.fragment.serialization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lj.library.R;
import com.lj.library.bean.serialization.GoogleProtobuf;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.Logger;

/**
 * Created by liujie_gyh on 16/7/20.
 */
public class SerializationFragment extends BaseFragment {

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        GoogleProtobuf.Person john = initPerson();
        Logger.i("原始的Person占内存的大小为%dByte", 0);
        Logger.i("序列化Person后,Person的大小为%dByte", john.toByteArray().length);
        deserializePerson(john.toByteArray());

    }

    private GoogleProtobuf.Person initPerson() {
        return GoogleProtobuf.Person.newBuilder()
                .setId(1234)
                .setName("John Doe")
                .setEmail("jdoe@example.com")
                .addPhone(GoogleProtobuf.Person.PhoneNumber.newBuilder()
                        .setNumber("555-4321")
                        .setType(GoogleProtobuf.Person.PhoneType.HOME))
                .build();
    }

    private void deserializePerson(byte[] bytes) {
        try {
            GoogleProtobuf.Person newJohn = GoogleProtobuf.Person.parseFrom(bytes);
            Logger.i("反序列化得到的Person的信息: %s", newJohn.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
