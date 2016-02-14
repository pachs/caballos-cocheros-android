package com.caballoscocheros;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static void main(String... args) throws Exception {
        Schema cc = new Schema(1, "com.caballoscocheros.db");



        new DaoGenerator().generateAll(cc, args[0]);
    }

}
