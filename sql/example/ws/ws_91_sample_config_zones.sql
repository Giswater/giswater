/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

SET search_path = "SCHEMA_NAME", public, pg_catalog;

INSERT INTO config_param_user VALUES (6, 'pipecat_vdefault', 'FC110-PN10', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (78, 'psector_measurement_vdefault', '1', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (11, 'tankcat_vdefault', 'TANK_01', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (12, 'hydrantcat_vdefault', 'HYDRANT 1X110', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (13, 'junctioncat_vdefault', 'JUNCTION DN110', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (14, 'pumpcat_vdefault', 'PUMP-01', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (15, 'valvecat_vdefault', 'AIR VALVE DN50', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (16, 'metercat_vdefault', 'PRESSUREMETERDN110 PN16', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (17, 'sourcecat_vdefault', 'SOURCE-01', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (18, 'filtercat_vdefault', 'FILTER-01-DN200', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (19, 'registercat_vdefault', 'REGISTER', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (20, 'netwjoincat_vdefault', 'WATER-CONNECTION', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (21, 'expansiontankcat_vdefault', 'EXPANTANK', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (22, 'flexuioncat_vdefault', 'FLEXUNION', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (23, 'netelementcat_vdefault', 'NETELEMENT', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (24, 'netsamplepointcat_vdefault', 'NETSAMPLEPOINT', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (25, 'municipality_vdefault', '1', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (26, 'greentapcat_vdefault', 'PVC50-PN16-GRE', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (27, 'wjoincat_vdefault', 'PVC25-PN16-DOM', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (28, 'fountaincat_vdefault', 'PVC63-PN16-FOU', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (29, 'tapcat_vdefault', 'PVC25-PN16-TAP', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (30, 'psector_scale_vdefault', '2.00', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (31, 'psector_rotation_vdefault', '0.0000', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (32, 'psector_gexpenses_vdefault', '19.00', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (33, 'psector_vat_vdefault', '21.00', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (34, 'psector_other_vdefault', '4.00', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (35, 'presszone_vdefault', 'HIGH', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (36, 'flexunioncat_vdefault', 'FLEXUNION', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (37, 'statetype_vdefault', 'OBSOLETE', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (38, 'arccat_vdefault', 'FC110-PN10', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (1, 'state_vdefault', '1', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (2, 'workcat_vdefault', 'WORK NO DATA', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (3, 'verified_vdefault', 'VERIFIED', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (4, 'builtdate_vdefault', '2017-12-06', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (5, 'enddate_vdefault', '2017-11-29', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (8, 'connecat_vdefault', 'PVC25-PN16-DOM', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (39, 'nodecat_vdefault', 'AIR VALVE DN50', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (40, 'sector_vdefault', 'sector_01', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (41, 'pavementcat_vdefault', 'Asphalt', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (9, 'elementcat_vdefault', 'COVER', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (10, 'exploitation_vdefault', '1', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (42, 'soilcat_vdefault', 'SOIL_01', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (43, 'dma_vdefault', '2', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (44, 'state_type_vdefault', '10', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (45, 'pavement_vdefault', 'Asphalt', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (46, 'wtpcat_vdefault', 'ETAP', NULL, 'postgres', NULL, NULL);
INSERT INTO config_param_user VALUES (79, 'psector_vdefault', 1, NULL, 'postgres', NULL, NULL);




INSERT INTO config_web_fields VALUES (2, 'review_arc', 'y1', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (14, 'review_node', 'ymax', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (47, 'review_connec', 'y1', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (49, 'review_connec', 'matcat_id', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_mat_arc', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (50, 'review_connec', 'y2', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (51, 'review_connec', 'arc_type', NULL, 'text', 18, NULL, NULL, NULL, 'QComboBox', 'arc_type', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (1, 'review_arc', 'arc_id', NULL, 'text', 16, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, false);
INSERT INTO config_web_fields VALUES (52, 'review_connec', 'shape', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_arc_shape', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (53, 'review_connec', 'geom1', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (54, 'review_connec', 'geom2', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (55, 'review_connec', 'annotation', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (56, 'review_connec', 'observ', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (57, 'review_connec', 'field_checked', NULL, 'boolean', NULL, NULL, NULL, NULL, 'QCheckBox', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (48, 'review_connec', 'connec_id', NULL, 'text', 16, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, false);
INSERT INTO config_web_fields VALUES (59, 'review_gully', 'arc_id', NULL, 'text', 16, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, false);
INSERT INTO config_web_fields VALUES (5, 'review_arc', 'matcat_id', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_mat_arc', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (3, 'review_arc', 'y2', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (4, 'review_arc', 'arc_type', NULL, 'text', 18, NULL, NULL, NULL, 'QComboBox', 'arc_type', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (62, 'review_gully', 'arc_type', NULL, 'text', 18, NULL, NULL, NULL, 'QComboBox', 'arc_type', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (63, 'review_gully', 'shape', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_arc_shape', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (6, 'review_arc', 'shape', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_arc_shape', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (64, 'review_gully', 'geom1', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (7, 'review_arc', 'geom1', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (65, 'review_gully', 'geom2', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (8, 'review_arc', 'geom2', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (9, 'review_arc', 'annotation', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (10, 'review_arc', 'observ', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (11, 'review_arc', 'field_checked', NULL, 'boolean', NULL, NULL, NULL, NULL, 'QCheckBox', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (66, 'review_gully', 'annotation', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (67, 'review_gully', 'observ', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (15, 'review_node', 'node_type', NULL, 'text', 18, NULL, NULL, NULL, 'QComboBox', 'node_type', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (16, 'review_node', 'matcat_id', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_mat_node', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (68, 'review_gully', 'field_checked', NULL, 'boolean', NULL, NULL, NULL, NULL, 'QCheckBox', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (17, 'review_node', 'shape', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_node_shape', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (61, 'review_gully', 'ymax', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (18, 'review_node', 'geom1', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (19, 'review_node', 'geom2', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (20, 'review_node', 'annotation', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (21, 'review_node', 'observ', NULL, 'text', 500, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (22, 'review_node', 'field_checked', NULL, 'boolean', NULL, NULL, NULL, NULL, 'QCheckBox', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (12, 'review_node', 'node_id', NULL, 'text', 16, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, false);
INSERT INTO config_web_fields VALUES (60, 'review_gully', 'connec_matcat', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_mat_arc', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (58, 'review_gully', 'top_elev', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (69, 'review_gully', 'sandbox', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (70, 'review_gully', 'groove', NULL, 'boolean', NULL, NULL, NULL, NULL, 'QCheckBox', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (71, 'review_gully', 'siphon', NULL, 'boolean', NULL, NULL, NULL, NULL, 'QCheckBox', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (73, 'review_gully', 'featurecat_id', NULL, 'text', 30, NULL, NULL, NULL, 'QComboBox', 'cat_feature', 'id', 'id', NULL, true);
INSERT INTO config_web_fields VALUES (74, 'review_gully', 'feature_id', NULL, 'text', 16, NULL, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);
INSERT INTO config_web_fields VALUES (13, 'review_node', 'top_elev', NULL, 'numeric', 12, 3, NULL, NULL, 'QLineEdit', NULL, NULL, NULL, NULL, true);

INSERT INTO macroexploitation VALUES (1, 'Macroexploitation-1', 'Macroexploitation-1', NULL);

INSERT INTO exploitation VALUES (1, 'expl_01', 1, NULL, NULL, '0106000020E764000001000000010300000001000000310000004344FB8D57901941579E3EEE847551414952F8205C901941B8E002D4967551412BEA86DD5E901941F1B6390E9D7551413CD9D0716790194146526D5EA2755141C252A53388901941664752E5A675514191608F1EC4901941107C78DAAA755141CD78F02601911941C9594B57AD7551414B4A73286091194199E45D54AF75514196093983DB9119419860ED49B175514149511D00F9911941A04440BDAF755141B5BE46E8AD921941115C602EA47551413252FCBE16941941AC9DABDF94755141C78F577EA29419415AE606D58E755141FCEF873869951941DBF98480867551414E302882CF95194168805EE581755141F6C32938F4961941D22EAC8E75755141BEF2646F3C9719414D0EBC8972755141B8A7F68B9A971941C8CDBE026E755141B22277D1FD97194193B3528968755141A8344C506D981941BC3C94776275514160A9766BB2981941E2E516F45E755141CA43B6C34F991941615273F3557551410137653BB1991941816A8CAD50755141B0C634E3339A1941036077F54A755141BE58D480839A1941224D3F97477551410BFAD15B709B1941806778B53D755141C2A48EBD199C19410F36E0BE367551419E9C3986F39B19416CBC9C1C33755141EAE67282AE9B1941FBE7A8EA3575514124193708FA9A1941AC2FF50B2C75514107D9C6CECB9A194101574A97297551414723F9E6179A19412C13ABA61F755141CAB0FBC0ED991941446A39E61C755141309C88F89098194121ABAC8A087551411D554E735E98194151FB59B705755141962F594D3D9819415F273EE5037551410EC2A5985A971941C7A4626AF77451416E6AC5DC6F961941F6EF74CCEA745141D6D7D6B2959519417289B548EF745141BEEC10BC4995194190769F9CF07451415C5A0852729419413182684F02755141DCE1D4EF6B94194131C106B705755141F1E448C7A1931941A4154AA615755141D1BA81A24E9319415BFD9E7E1975514154BF5362D4911941BB61D7152975514169EECA68629019410ED2733838755141CEE8BC0E43901941490775783A755141253AB8DB34901941CEE66D84407551414344FB8D57901941579E3EEE84755141', '2017-12-22 12:30:52.569543');
INSERT INTO exploitation VALUES (2, 'expl_02', 1, NULL, NULL, '0106000020E76400000100000001030000000100000020000000BBD70605668A1941BEB94A87BF765141584CD18B848919412D294EC3B57651411A1991F0AE881941D9ED61A0AB76514113D980EEA3871941BAB108279E7651418474A44FDB871941D13E8274997651410A7D84E9FE8719412ECCCADE957651417CD969941C881941B20229FB8B765141736CAD5B3088194158565FEF84765141E5C892064E88194185AA331D80765141380CC18979881941F15318EC7A7651416C3638E5B28819416C19991F74765141EDD15B46EA8819416A34F9106E76514186197A96278919413FC1A341687651410548E1BE72891941F8DB3416617651411213A8BBF98919415DF5BA5A5476514133A1A8E86F8A194150BA9D544B765141CC0E4EC7D48A1941CA7F1E8844765141F5F18F78498B1941BA7EE2144076514121AF4A9B968B1941E3F137E445765141CB473921268D1941F5F273574A765141A02F5445258E1941692CB7B04C765141DAFDCE9BB98E1941849ED00C4E76514132DC37DAA38E19418C2E2F9755765141DF980957788E194172C036EB64765141061F7DB7568E1941788A760871765141828359561F8E1941C339255184765141A5374B39D08D194174EB1A9DA1765141115DE9217F8D1941ECF2BEC3C076514134E35C825D8D19414276DAABCE76514132074079A28B1941F4821D4EC87651412191BA001A8B1941531066B8C4765141BBD70605668A1941BEB94A87BF765141', '2018-01-22 14:30:19.434152');

INSERT INTO macrodma VALUES (1, 'macrodma_01', 1, NULL, NULL, '0103000020E764000001000000310000004344FB8D57901941579E3EEE847551414952F8205C901941B8E002D4967551412BEA86DD5E901941F1B6390E9D7551413CD9D0716790194146526D5EA2755141C252A53388901941664752E5A675514191608F1EC4901941107C78DAAA755141CD78F02601911941C9594B57AD7551414B4A73286091194199E45D54AF75514196093983DB9119419860ED49B175514149511D00F9911941A04440BDAF755141B5BE46E8AD921941115C602EA47551413252FCBE16941941AC9DABDF94755141C78F577EA29419415AE606D58E755141FCEF873869951941DBF98480867551414E302882CF95194168805EE581755141F6C32938F4961941D22EAC8E75755141BEF2646F3C9719414D0EBC8972755141B8A7F68B9A971941C8CDBE026E755141B22277D1FD97194193B3528968755141A8344C506D981941BC3C94776275514160A9766BB2981941E2E516F45E755141CA43B6C34F991941615273F3557551410137653BB1991941816A8CAD50755141B0C634E3339A1941036077F54A755141BE58D480839A1941224D3F97477551410BFAD15B709B1941806778B53D755141C2A48EBD199C19410F36E0BE367551419E9C3986F39B19416CBC9C1C33755141EAE67282AE9B1941FBE7A8EA3575514124193708FA9A1941AC2FF50B2C75514107D9C6CECB9A194101574A97297551414723F9E6179A19412C13ABA61F755141CAB0FBC0ED991941446A39E61C755141309C88F89098194121ABAC8A087551411D554E735E98194151FB59B705755141962F594D3D9819415F273EE5037551410EC2A5985A971941C7A4626AF77451416E6AC5DC6F961941F6EF74CCEA745141D6D7D6B2959519417289B548EF745141BEEC10BC4995194190769F9CF07451415C5A0852729419413182684F02755141DCE1D4EF6B94194131C106B705755141F1E448C7A1931941A4154AA615755141D1BA81A24E9319415BFD9E7E1975514154BF5362D4911941BB61D7152975514169EECA68629019410ED2733838755141CEE8BC0E43901941490775783A755141253AB8DB34901941CEE66D84407551414344FB8D57901941579E3EEE84755141');
INSERT INTO macrodma VALUES (2, 'macrodma_02', 2, NULL, NULL, '0103000020E7640000010000002000000032074079A28B1941F4821D4EC8765141BBD70605668A1941BEB94A87BF765141584CD18B848919412D294EC3B57651411A1991F0AE881941D9ED61A0AB76514113D980EEA3871941BAB108279E7651418474A44FDB871941D13E8274997651410A7D84E9FE8719412ECCCADE957651417CD969941C881941B20229FB8B765141736CAD5B3088194158565FEF84765141E5C892064E88194185AA331D80765141380CC18979881941F15318EC7A7651416C3638E5B28819416C19991F74765141EDD15B46EA8819416A34F9106E7651410548E1BE72891941F8DB3416617651411213A8BBF98919415DF5BA5A5476514133A1A8E86F8A194150BA9D544B765141CC0E4EC7D48A1941CA7F1E8844765141F5F18F78498B1941BA7EE2144076514121AF4A9B968B1941E3F137E4457651416E7F8844AA8C1941818B7CEF48765141CB473921268D1941F5F273574A765141DAFDCE9BB98E1941849ED00C4E765141DF980957788E194172C036EB64765141061F7DB7568E1941788A760871765141828359561F8E1941C3392551847651417CC8161ADB8D19416EA3EBD79D765141EFBF3680B78D19418DDF4451AB765141776351D5998D1941221B0091B6765141115DE9217F8D1941ECF2BEC3C076514134E35C825D8D19414276DAABCE7651411F0812C5938C194137205DB4CB76514132074079A28B1941F4821D4EC8765141');

INSERT INTO dma VALUES (2, 'dma_01', 1, NULL, NULL, NULL, '0103000020E764000001000000310000004344FB8D57901941579E3EEE847551414952F8205C901941B8E002D4967551412BEA86DD5E901941F1B6390E9D7551413CD9D0716790194146526D5EA2755141C252A53388901941664752E5A675514191608F1EC4901941107C78DAAA755141CD78F02601911941C9594B57AD7551414B4A73286091194199E45D54AF75514196093983DB9119419860ED49B175514149511D00F9911941A04440BDAF755141B5BE46E8AD921941115C602EA47551413252FCBE16941941AC9DABDF94755141C78F577EA29419415AE606D58E755141FCEF873869951941DBF98480867551414E302882CF95194168805EE581755141F6C32938F4961941D22EAC8E75755141BEF2646F3C9719414D0EBC8972755141B8A7F68B9A971941C8CDBE026E755141B22277D1FD97194193B3528968755141A8344C506D981941BC3C94776275514160A9766BB2981941E2E516F45E755141CA43B6C34F991941615273F3557551410137653BB1991941816A8CAD50755141B0C634E3339A1941036077F54A755141BE58D480839A1941224D3F97477551410BFAD15B709B1941806778B53D755141C2A48EBD199C19410F36E0BE367551419E9C3986F39B19416CBC9C1C33755141EAE67282AE9B1941FBE7A8EA3575514124193708FA9A1941AC2FF50B2C75514107D9C6CECB9A194101574A97297551414723F9E6179A19412C13ABA61F755141CAB0FBC0ED991941446A39E61C755141309C88F89098194121ABAC8A087551411D554E735E98194151FB59B705755141962F594D3D9819415F273EE5037551410EC2A5985A971941C7A4626AF77451416E6AC5DC6F961941F6EF74CCEA745141D6D7D6B2959519417289B548EF745141BEEC10BC4995194190769F9CF07451415C5A0852729419413182684F02755141DCE1D4EF6B94194131C106B705755141F1E448C7A1931941A4154AA615755141D1BA81A24E9319415BFD9E7E1975514154BF5362D4911941BB61D7152975514169EECA68629019410ED2733838755141CEE8BC0E43901941490775783A755141253AB8DB34901941CEE66D84407551414344FB8D57901941579E3EEE84755141');
INSERT INTO dma VALUES (3, 'dma_02', 2, NULL, NULL, NULL, '0103000020E7640000010000001F000000BBD70605668A1941BEB94A87BF765141584CD18B848919412D294EC3B57651411A1991F0AE881941D9ED61A0AB76514113D980EEA3871941BAB108279E7651418474A44FDB871941D13E8274997651410A7D84E9FE8719412ECCCADE957651417CD969941C881941B20229FB8B765141736CAD5B3088194158565FEF84765141E5C892064E88194185AA331D80765141380CC18979881941F15318EC7A7651416C3638E5B28819416C19991F74765141EDD15B46EA8819416A34F9106E76514186197A96278919413FC1A341687651410548E1BE72891941F8DB3416617651411213A8BBF98919415DF5BA5A5476514133A1A8E86F8A194150BA9D544B765141CC0E4EC7D48A1941CA7F1E8844765141F5F18F78498B1941BA7EE2144076514121AF4A9B968B1941E3F137E445765141CB473921268D1941F5F273574A765141014432FA8C8D19416281F8344B765141DAFDCE9BB98E1941849ED00C4E76514132DC37DAA38E19418C2E2F9755765141DF980957788E194172C036EB64765141061F7DB7568E1941788A760871765141828359561F8E1941C339255184765141A5374B39D08D194174EB1A9DA1765141115DE9217F8D1941ECF2BEC3C076514134E35C825D8D19414276DAABCE76514132074079A28B1941F4821D4EC8765141BBD70605668A1941BEB94A87BF765141');

INSERT INTO sector VALUES (2, 'sector_01', 'sector_sample_epanet', NULL, '0103000020E764000001000000310000004344FB8D57901941579E3EEE847551414952F8205C901941B8E002D4967551412BEA86DD5E901941F1B6390E9D7551413CD9D0716790194146526D5EA2755141C252A53388901941664752E5A675514191608F1EC4901941107C78DAAA755141CD78F02601911941C9594B57AD7551414B4A73286091194199E45D54AF75514196093983DB9119419860ED49B175514149511D00F9911941A04440BDAF755141B5BE46E8AD921941115C602EA47551413252FCBE16941941AC9DABDF94755141C78F577EA29419415AE606D58E755141FCEF873869951941DBF98480867551414E302882CF95194168805EE581755141F6C32938F4961941D22EAC8E75755141BEF2646F3C9719414D0EBC8972755141B8A7F68B9A971941C8CDBE026E755141B22277D1FD97194193B3528968755141A8344C506D981941BC3C94776275514160A9766BB2981941E2E516F45E755141CA43B6C34F991941615273F3557551410137653BB1991941816A8CAD50755141B0C634E3339A1941036077F54A755141BE58D480839A1941224D3F97477551410BFAD15B709B1941806778B53D755141C2A48EBD199C19410F36E0BE367551419E9C3986F39B19416CBC9C1C33755141EAE67282AE9B1941FBE7A8EA3575514124193708FA9A1941AC2FF50B2C75514107D9C6CECB9A194101574A97297551414723F9E6179A19412C13ABA61F755141CAB0FBC0ED991941446A39E61C755141309C88F89098194121ABAC8A087551411D554E735E98194151FB59B705755141962F594D3D9819415F273EE5037551410EC2A5985A971941C7A4626AF77451416E6AC5DC6F961941F6EF74CCEA745141D6D7D6B2959519417289B548EF745141BEEC10BC4995194190769F9CF07451415C5A0852729419413182684F02755141DCE1D4EF6B94194131C106B705755141F1E448C7A1931941A4154AA615755141D1BA81A24E9319415BFD9E7E1975514154BF5362D4911941BB61D7152975514169EECA68629019410ED2733838755141CEE8BC0E43901941490775783A755141253AB8DB34901941CEE66D84407551414344FB8D57901941579E3EEE84755141');
INSERT INTO sector VALUES (3, 'sector_02', 'sector_2_sample_epanet', NULL, '0103000020E76400000100000020000000F645AAE0958D1941CBF046DDB776514134E35C825D8D19414276DAABCE76514132074079A28B1941F4821D4EC8765141BBD70605668A1941BEB94A87BF765141584CD18B848919412D294EC3B57651411A1991F0AE881941D9ED61A0AB7651419CF220BC0E881941217A6E97A376514113D980EEA3871941BAB108279E7651418474A44FDB871941D13E8274997651410A7D84E9FE8719412ECCCADE95765141736CAD5B3088194158565FEF84765141E5C892064E88194185AA331D80765141380CC18979881941F15318EC7A7651416C3638E5B28819416C19991F74765141EDD15B46EA8819416A34F9106E76514186197A96278919413FC1A341687651410548E1BE72891941F8DB3416617651411213A8BBF98919415DF5BA5A5476514133A1A8E86F8A194150BA9D544B765141CC0E4EC7D48A1941CA7F1E8844765141F5F18F78498B1941BA7EE2144076514121AF4A9B968B1941E3F137E445765141CB473921268D1941F5F273574A765141A02F5445258E1941692CB7B04C765141DAFDCE9BB98E1941849ED00C4E76514132DC37DAA38E19418C2E2F9755765141670EA629888E19417886555860765141061F7DB7568E1941788A760871765141828359561F8E1941C339255184765141AAB5C7A5038E194154CA21158E765141A5374B39D08D194174EB1A9DA1765141F645AAE0958D1941CBF046DDB7765141');

INSERT INTO plan_psector_cat_type VALUES ('1', 'Planificated');
INSERT INTO plan_psector_cat_type VALUES ('2', 'Reconstruction');
INSERT INTO plan_psector_cat_type VALUES ('3', 'Rehabilitation');

INSERT INTO plan_psector VALUES (1, 'Masterplan 01', 1, 'Expanding the capacity of the pipes located on Francesc Layret street.', 1, 'NORMAL_PRIORITY', NULL, NULL, 'Action caused by the headloss of the pipe.', NULL, 750.00, 2, '01', 19.00, 21.00, 0.00, TRUE, '0106000020E7640000010000000103000000010000000500000062EBC6A727941941B58BF9D6467551417EBB5BC63A961941B58BF9D6467551417EBB5BC63A961941057CDEC53675514162EBC6A727941941047CDEC53675514162EBC6A727941941B58BF9D646755141');
INSERT INTO plan_psector VALUES (2, 'Masterplan 02', 1, 'Expanding the capacity of the pipes located on Legalitat street.', 1, 'LOW_PRIORITY', NULL, NULL, 'Action caused by the headloss of the pipe.', 90.0000, 750.00, 2, '02', 19.00, 21.00, 0.00, TRUE, '0106000020E7640000010000000103000000010000000500000076C2E9BF34941941DBAB60D39875514166D2E9BF34941941DA5E77A17775514166D738AE339319415E5E77A17775514166C738AE339319415FAB60D39875514176C2E9BF34941941DBAB60D398755141');

