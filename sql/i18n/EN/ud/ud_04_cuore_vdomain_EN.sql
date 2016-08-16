/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


-- ----------------------------
-- Records of value_state
-- ----------------------------
INSERT INTO "value_state" VALUES ('OBSOLETE');
INSERT INTO "value_state" VALUES ('ON_SERVICE');
INSERT INTO "value_state" VALUES ('RECONSTRUCT');
INSERT INTO "value_state" VALUES ('REPLACE');
INSERT INTO "value_state" VALUES ('PLANIFIED');


-- ----------------------------
-- Records of value_verified
-- ----------------------------
INSERT INTO "value_verified" VALUES ('TO REVIEW');
INSERT INTO "value_verified" VALUES ('VERIFIED');


-- ----------------------------
-- Records of value_yesno
-- ----------------------------
INSERT INTO "value_yesno" VALUES ('NO');
INSERT INTO "value_yesno" VALUES ('YES');


-- ----------------------------
-- Records of connec_type
-- ----------------------------
INSERT INTO connec_type VALUES ('DOMESTIC', NULL);
INSERT INTO connec_type VALUES ('TRADE', NULL);
INSERT INTO connec_type VALUES ('INDUSTRIAL', NULL);


-- ----------------------------
-- Records of man_type_category
-- ----------------------------
INSERT INTO "man_type_category" VALUES ('NO CATEGORY DATA', null);


-- ----------------------------
-- Records of man_type_fluid
-- ----------------------------
INSERT INTO "man_type_fluid" VALUES ('NO FLUID DATA', null);


-- ----------------------------
-- Records of man_type_location
-- ----------------------------
INSERT INTO "man_type_location" VALUES ('NO LOCATION DATA', null);