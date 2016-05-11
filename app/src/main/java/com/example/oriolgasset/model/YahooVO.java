package com.example.oriolgasset.model;

/**
 * Created by Oriol on 11/5/2016.
 */
public class YahooVO {
    public String imageUrl;

    public Condition condition = new Condition();
    public Wind wind = new Wind();
    public Atmosphere atmosphere = new Atmosphere();
    public Forecast forecast = new Forecast();
    public Location location = new Location();
    public Astronomy astronomy = new Astronomy();
    public Units units = new Units();

    public String lastUpdate;

    public static class Atmosphere {
        public int humidity;
        public float visibility;
        public float pressure;
        public int rising;
    }

    public class Condition {
        public String description;
        public int code;
        public String date;
        public int temp;
    }

    public class Forecast {
        public int tempMin;
        public int tempMax;
        public String description;
        public int code;
    }

    public class Wind {
        public int chill;
        public int direction;
        public int speed;
    }

    public class Units {
        public String speed;
        public String distance;
        public String pressure;
        public String temperature;
    }

    public class Location {
        public String name;
        public String region;
        public String country;
    }

    public class Astronomy {
        public String sunRise;
        public String sunSet;
    }
}
