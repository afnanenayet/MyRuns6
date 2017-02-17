package com.afnanenayet.afnan_enayet_myruns6;

// Generated with Weka 3.8.1
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Tue Feb 14 17:11:07 EST 2017

/**
 * A J48 decision tree that classifies accelerometer data into activity types
 */
class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N5f9d223d0(i);
        return p;
    }

    static double N5f9d223d0(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 94.349691) {
            p = WekaClassifier.N287f7c7a1(i);
        } else if (((Double) i[0]).doubleValue() > 94.349691) {
            p = WekaClassifier.N13a10b6625(i);
        }
        return p;
    }

    static double N287f7c7a1(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 33.155105) {
            p = WekaClassifier.N1cda01dc2(i);
        } else if (((Double) i[0]).doubleValue() > 33.155105) {
            p = WekaClassifier.N2ad6c2ed8(i);
        }
        return p;
    }

    static double N1cda01dc2(Object[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 0;
        } else if (((Double) i[9]).doubleValue() <= 0.65484) {
            p = 0;
        } else if (((Double) i[9]).doubleValue() > 0.65484) {
            p = WekaClassifier.N673536643(i);
        }
        return p;
    }

    static double N673536643(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 25.971268) {
            p = WekaClassifier.N518f6c754(i);
        } else if (((Double) i[0]).doubleValue() > 25.971268) {
            p = WekaClassifier.N4e5873f76(i);
        }
        return p;
    }

    static double N518f6c754(Object[] i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 0;
        } else if (((Double) i[7]).doubleValue() <= 1.155661) {
            p = 0;
        } else if (((Double) i[7]).doubleValue() > 1.155661) {
            p = WekaClassifier.N2c046925(i);
        }
        return p;
    }

    static double N2c046925(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 20.15815) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 20.15815) {
            p = 3;
        }
        return p;
    }

    static double N4e5873f76(Object[] i) {
        double p = Double.NaN;
        if (i[23] == null) {
            p = 3;
        } else if (((Double) i[23]).doubleValue() <= 0.190399) {
            p = 3;
        } else if (((Double) i[23]).doubleValue() > 0.190399) {
            p = WekaClassifier.N90ea4e7(i);
        }
        return p;
    }

    static double N90ea4e7(Object[] i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() <= 0.703153) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() > 0.703153) {
            p = 3;
        }
        return p;
    }

    static double N2ad6c2ed8(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 47.786803) {
            p = WekaClassifier.N2a22f5f39(i);
        } else if (((Double) i[0]).doubleValue() > 47.786803) {
            p = WekaClassifier.N167b7cf117(i);
        }
        return p;
    }

    static double N2a22f5f39(Object[] i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = 0;
        } else if (((Double) i[28]).doubleValue() <= 0.158853) {
            p = WekaClassifier.N2768ec8b10(i);
        } else if (((Double) i[28]).doubleValue() > 0.158853) {
            p = WekaClassifier.N3cf0cff012(i);
        }
        return p;
    }

    static double N2768ec8b10(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 2.662741) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() > 2.662741) {
            p = WekaClassifier.N202db0b211(i);
        }
        return p;
    }

    static double N202db0b211(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 7.579467) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 7.579467) {
            p = 3;
        }
        return p;
    }

    static double N3cf0cff012(Object[] i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 3;
        } else if (((Double) i[20]).doubleValue() <= 0.141492) {
            p = 3;
        } else if (((Double) i[20]).doubleValue() > 0.141492) {
            p = WekaClassifier.N1c038f013(i);
        }
        return p;
    }

    static double N1c038f013(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 1.934609) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() > 1.934609) {
            p = WekaClassifier.N61ac80f714(i);
        }
        return p;
    }

    static double N61ac80f714(Object[] i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 0;
        } else if (((Double) i[12]).doubleValue() <= 0.364403) {
            p = 0;
        } else if (((Double) i[12]).doubleValue() > 0.364403) {
            p = WekaClassifier.N2cc9045015(i);
        }
        return p;
    }

    static double N2cc9045015(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 3.305839) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() > 3.305839) {
            p = WekaClassifier.N167cd17616(i);
        }
        return p;
    }

    static double N167cd17616(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 4.785737) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() > 4.785737) {
            p = 3;
        }
        return p;
    }

    static double N167b7cf117(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 12.751204) {
            p = WekaClassifier.N13d5fdf118(i);
        } else if (((Double) i[1]).doubleValue() > 12.751204) {
            p = 3;
        }
        return p;
    }

    static double N13d5fdf118(Object[] i) {
        double p = Double.NaN;
        if (i[22] == null) {
            p = 3;
        } else if (((Double) i[22]).doubleValue() <= 0.524169) {
            p = WekaClassifier.N77da805f19(i);
        } else if (((Double) i[22]).doubleValue() > 0.524169) {
            p = WekaClassifier.N55f1987e23(i);
        }
        return p;
    }

    static double N77da805f19(Object[] i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 3;
        } else if (((Double) i[10]).doubleValue() <= 0.744552) {
            p = WekaClassifier.N75d18b320(i);
        } else if (((Double) i[10]).doubleValue() > 0.744552) {
            p = 3;
        }
        return p;
    }

    static double N75d18b320(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 4.290446) {
            p = WekaClassifier.N4a8c705d21(i);
        } else if (((Double) i[4]).doubleValue() > 4.290446) {
            p = 0;
        }
        return p;
    }

    static double N4a8c705d21(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 0.671712) {
            p = WekaClassifier.N768a368822(i);
        } else if (((Double) i[6]).doubleValue() > 0.671712) {
            p = 3;
        }
        return p;
    }

    static double N768a368822(Object[] i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if (((Double) i[9]).doubleValue() <= 0.485503) {
            p = 3;
        } else if (((Double) i[9]).doubleValue() > 0.485503) {
            p = 0;
        }
        return p;
    }

    static double N55f1987e23(Object[] i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 3;
        } else if (((Double) i[18]).doubleValue() <= 0.515577) {
            p = 3;
        } else if (((Double) i[18]).doubleValue() > 0.515577) {
            p = WekaClassifier.N3a869af324(i);
        }
        return p;
    }

    static double N3a869af324(Object[] i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 0;
        } else if (((Double) i[64]).doubleValue() <= 1.984667) {
            p = 0;
        } else if (((Double) i[64]).doubleValue() > 1.984667) {
            p = 1;
        }
        return p;
    }

    static double N13a10b6625(Object[] i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 15.601122) {
            p = WekaClassifier.N34b841ec26(i);
        } else if (((Double) i[64]).doubleValue() > 15.601122) {
            p = WekaClassifier.Nb6e634(i);
        }
        return p;
    }

    static double N34b841ec26(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 33.870892) {
            p = WekaClassifier.N6dd31d6d27(i);
        } else if (((Double) i[3]).doubleValue() > 33.870892) {
            p = WekaClassifier.N6e7c6ea433(i);
        }
        return p;
    }

    static double N6dd31d6d27(Object[] i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 19.416254) {
            p = WekaClassifier.N638c432628(i);
        } else if (((Double) i[7]).doubleValue() > 19.416254) {
            p = 3;
        }
        return p;
    }

    static double N638c432628(Object[] i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 3;
        } else if (((Double) i[16]).doubleValue() <= 0.170006) {
            p = 3;
        } else if (((Double) i[16]).doubleValue() > 0.170006) {
            p = WekaClassifier.N786d8e0a29(i);
        }
        return p;
    }

    static double N786d8e0a29(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 221.876736) {
            p = WekaClassifier.Ne2c17ca30(i);
        } else if (((Double) i[0]).doubleValue() > 221.876736) {
            p = 1;
        }
        return p;
    }

    static double Ne2c17ca30(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 12.435291) {
            p = WekaClassifier.N7be14b1731(i);
        } else if (((Double) i[4]).doubleValue() > 12.435291) {
            p = 3;
        }
        return p;
    }

    static double N7be14b1731(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 3.063042) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() > 3.063042) {
            p = WekaClassifier.N7a75a23e32(i);
        }
        return p;
    }

    static double N7a75a23e32(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() <= 31.56032) {
            p = 3;
        } else if (((Double) i[1]).doubleValue() > 31.56032) {
            p = 1;
        }
        return p;
    }

    static double N6e7c6ea433(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 444.282019) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > 444.282019) {
            p = 2;
        }
        return p;
    }

    static double Nb6e634(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 595.153923) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > 595.153923) {
            p = 2;
        }
        return p;
    }
}

