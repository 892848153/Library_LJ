package com.lj.library.fragment.serialization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lj.library.R;
import com.lj.library.bean.serialization.GoogleProtobuf;
import com.lj.library.bean.serialization.WirePerson;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.IOStreamUtils;
import com.lj.library.util.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 几种序列化方式的对比.
 * Created by liujie_gyh on 16/7/20.
 */
public class SerializationFragment extends BaseFragment {

    private static final int ID = 1234;
    private static final String NAME = "John Doe";
    private static final String EMAIL = "jdoe@example.com";
    private static final String PHONE_NUMBER = "555-4321";

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        javaSerializationTest();
        googleProtobufTest();
        squareWireTest();
    }

    /**
     * ------------------------Java Serialization的序列化方式------------------------------
     */
    private void javaSerializationTest() {
        JavaPerson john = initJavaPerson();
        Logger.i("java serialization方式序列化自己写的Person, Person的大小为%dByte",
                IOStreamUtils.getSerialBytes(john).length);
    }

    private JavaPerson initJavaPerson() {
        JavaPerson.PhoneNumber phoneNumber = new JavaPerson.PhoneNumber();
        phoneNumber.number = PHONE_NUMBER;
        phoneNumber.type = JavaPerson.PhoneType.HOME;
        List phones = new ArrayList<>();
        phones.add(phoneNumber);

        JavaPerson john = new JavaPerson();
        john.id = ID;
        john.name  = NAME;
        john.email = EMAIL;
        john.phones = phones;

        return john;
    }

    private static class JavaPerson implements Serializable {
        String name;
        int id;
        String email;
        List<PhoneNumber> phones;

        public static final class PhoneNumber implements Serializable {
            String number;
            PhoneType type;
        }

        public enum PhoneType {
            MOBILE,
            HOME,
            WORK
        }
    }

    /**
     * ------------------------Google Protobuf的序列化方式------------------------------
     */
    private void googleProtobufTest() {
        GoogleProtobuf.Person john = initPerson();
        Logger.i("java serialization方式序列化Google Protobuf生成的Person, Person的大小为%dByte",
                IOStreamUtils.getSerialBytes(john).length);
        Logger.i("google protobuf序列化Person后,Person的大小为%dByte", john.toByteArray().length);
        deserializePerson(john.toByteArray());
    }

    private GoogleProtobuf.Person initPerson() {
        return GoogleProtobuf.Person.newBuilder()
                .setId(ID)
                .setName(NAME)
                .setEmail(EMAIL)
                .addPhone(GoogleProtobuf.Person.PhoneNumber.newBuilder()
                        .setNumber(PHONE_NUMBER)
                        .setType(GoogleProtobuf.Person.PhoneType.HOME))
                .build();
    }

    private void deserializePerson(byte[] bytes) {
        try {
            GoogleProtobuf.Person newJohn = GoogleProtobuf.Person.parseFrom(bytes);
            Logger.i("google protobuf反序列化得到的Person的信息: %s", newJohn.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * ------------------------Square Wire的序列化方式------------------------------
     */
    private void squareWireTest() {
        WirePerson john = initWirePerson();
        Logger.i("java serialization方式序列化Square Wire生成的Person, Person的大小为%dByte",
                IOStreamUtils.getSerialBytes(john).length);
        Logger.i("square wire序列化Person后,Person的大小为%dByte", WirePerson.ADAPTER.encode(john).length);
        deserializeWirePerson(WirePerson.ADAPTER.encode(john));
    }

    private WirePerson initWirePerson() {
        WirePerson.PhoneNumber phoneNumber = new WirePerson.PhoneNumber.Builder()
                .number(PHONE_NUMBER)
                .type(WirePerson.PhoneType.HOME).build();
        List phones = new ArrayList<>();
        phones.add(phoneNumber);
        WirePerson person = new WirePerson.Builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .phoneNumber(phones)
                .build();
        return person;
    }

    private void deserializeWirePerson(byte[] bytes) {
        try {
            WirePerson newJohn = WirePerson.ADAPTER.decode(bytes);
            Logger.i("square wire反序列化得到的Person的信息: %s", newJohn.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
