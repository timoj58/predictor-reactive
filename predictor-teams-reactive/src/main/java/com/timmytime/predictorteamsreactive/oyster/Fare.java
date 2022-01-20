package com.timmytime.predictorteamsreactive.oyster;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public enum Fare {

    ZONE_1(2.50),
    ONE_OUT(2.00),
    TWO_OUT_INC_Z1(3.00),
    TWO_OUT_EXC_Z1(2.25),
    THREE(3.20),
    BUS(1.80),
    MAX(3.20);

    @Getter
    private final double price;

    Fare(double price) {
        this.price = price;
    }

    /*
        Anywhere in Zone 1 £2.50
Any one zone outside zone 1 £2.00
Any two zones including zone 1 £3.00
Any two zones excluding zone 1 £2.25
Any three zones £3.20
Any bus journey £1.80
The maximum possible fare is therefore £3.20
     */

    public static Fare calcFare(Station in, Station out) {

        List<Fare> possibleFares = new ArrayList<>();
        possibleFares.add(MAX);

        if (in.getZones().contains(1) && out.getZones().contains(1)) {
            possibleFares.add(ZONE_1);
        }

        if (!in.getZones().stream().allMatch(m -> m == 1)
                && !out.getZones().stream().allMatch(m -> m == 1)
                && zonesTravelled(in.getZones(), out.getZones()) == 1) {
            possibleFares.add(ONE_OUT);
        }

        if (((in.getZones().contains(1) && in.getZones().size() == 1)
                || (out.getZones().contains(1) && out.getZones().size() == 1))
                && zonesTravelled(in.getZones(), out.getZones()) == 2) {
            possibleFares.add(TWO_OUT_INC_Z1);
        }

        if (!in.getZones().stream().allMatch(m -> m == 1) && !out.getZones().stream().allMatch(m -> m == 1)
                && zonesTravelled(in.getZones(), out.getZones()) == 2) {
            possibleFares.add(TWO_OUT_EXC_Z1);
        }

        if (zonesTravelled(in.getZones(), out.getZones()) == 3) {
            possibleFares.add(THREE);
        }

        if (in.equals(Station.BUS_STOP)) {
            possibleFares.add(BUS);
        }

        //get lowest fare...
        return possibleFares.stream()
                .min(Comparator.comparingDouble(Fare::getPrice))
                .get();

    }

    public static Integer zonesTravelled(List<Integer> in, List<Integer> out) {
        List<Integer> zones = new ArrayList<>();
        in.forEach(i -> out.forEach(o -> zones.add(Math.abs(i - o))));

        return zones.stream().min(Integer::compareTo).get() + 1;
    }

}
