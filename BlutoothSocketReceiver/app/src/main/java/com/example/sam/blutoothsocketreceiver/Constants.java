package com.example.sam.blutoothsocketreceiver;


import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final Map<String, String> dataBases = new HashMap<>();
    public static final String dataBaseUrl = "https://1678-scouting-2016.firebaseio.com/";
    static {
        dataBases.put("https://1678-scouting-2016.firebaseio.com/", "qVIARBnAD93iykeZSGG8mWOwGegminXUUGF2q0ee");
        dataBases.put("https://1678-dev3-2016.firebaseio.com/", "AEduO6VFlZKD4v10eW81u9j3ZNopr5h2R32SPpeq");
        dataBases.put("https://1678-dev-2016.firebaseio.com/", "j1r2wo3RUPMeUZosxwvVSFEFVcrXuuMAGjk6uPOc");
        dataBases.put("https://1678-dev2-2016.firebaseio.com/", "hL8fStivTbHUXM8A0KXBYPg2cMsl80EcD7vgwJ1u");
        dataBases.put("https://1678-extreme-testing.firebaseio.com/", "lGufYCifprPw8p1fiVOs7rqYV3fswHHr9YLwiUWh");

    }

}
