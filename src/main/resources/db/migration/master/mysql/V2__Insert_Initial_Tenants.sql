-- ==================================================================================
-- MIGRATION: Insert_50_Tenants.sql
-- DESCRIÇÃO: Insere 50 tenants de teste na master database
-- ==================================================================================
-- Insert default plans first (if not exist)
INSERT IGNORE INTO TB_TNT_PLAN (ID, ACTIVE, CREATED_AT, DESCRIPTION, MAX_USERS, MONTHLY_PRICE, NAME)
VALUES (1, 1, NOW(), 'Plano Básico', 10, 99.99, 'BASIC'),
       (2, 1, NOW(), 'Plano Profissional', 50, 299.99, 'PRO'),
       (3, 1, NOW(), 'Plano Empresarial', 500, 999.99, 'ENTERPRISE');


-- Tenant 1: HECE Distribuidora
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (1, 1, NOW(), 'contact@hece.test', 'HECE Distribuidora', '(11) 9999-8888', 0, 1);


-- Tenant 2: Tech Solutions
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (2,
        1,
        NOW(),
        'contact@techsolutions.test',
        'Tech Solutions',
        '(11) 8888-7777',
        0,
        1);


-- Tenant 3: Logística Global
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (3,
        1,
        NOW(),
        'contact@logistica.test',
        'Logística Global',
        '(21) 7777-6666',
        0,
        1);


-- Tenant 4: Varejo Express
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (4, 1, NOW(), 'contact@varejo.test', 'Varejo Express', '(31) 6666-5555', 0, 1);


-- Tenant 5: FarmaSaúde
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (5, 1, NOW(), 'contact@farmasaude.test', 'FarmaSaúde', '(41) 5555-4444', 0, 1);


-- Tenant 6: AutoPeças Brasil
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (6,
        1,
        NOW(),
        'contact@autopecas.test',
        'AutoPeças Brasil',
        '(51) 4444-3333',
        0,
        1);


-- Tenant 7: Construmax
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (7, 1, NOW(), 'contact@construmax.test', 'Construmax', '(61) 3333-2222', 0, 1);


-- Tenant 8: EletroTech
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (8, 1, NOW(), 'contact@eletrotech.test', 'EletroTech', '(71) 2222-1111', 0, 1);


-- Tenant 9: Alimentos Naturais
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (9,
        1,
        NOW(),
        'contact@alimentosnaturais.test',
        'Alimentos Naturais',
        '(81) 1111-0000',
        0,
        1);


-- Tenant 10: ModaStyle
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (10, 1, NOW(), 'contact@modastyle.test', 'ModaStyle', '(85) 9999-1234', 0, 1);


# -- Tenant 11: InfoSystems
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (11, 1, NOW(), 'contact@infosystems.test', 'InfoSystems', '(11) 9999-5678', 0, 1);


# -- Tenant 12: GreenAgro
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (12, 1, NOW(), 'contact@greenagro.test', 'GreenAgro', '(21) 8888-5678', 0, 1);


# -- Tenant 13: MegaAtacado
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (13, 1, NOW(), 'contact@megaatacado.test', 'MegaAtacado', '(31) 7777-5678', 0, 1);


-- Tenant 14: PetShop Amigo
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (14,
        1,
        NOW(),
        'contact@petshopamigo.test',
        'PetShop Amigo',
        '(41) 6666-5678',
        0,
        1);


-- Tenant 15: BeautyCenter
# INSERT IGNORE INTO tb_tnt (id, active, created_at, email, NAME, phone, trial, plan_id)
# VALUES
#     (
#         15,
#         1,
#         NOW(),
#         'contact@beautycenter.test',
#         'BeautyCenter',
#         '(51) 5555-5678',
#         0,
#         1
#     );


-- Tenant 16: SportMax
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (16, 1, NOW(), 'contact@sportmax.test', 'SportMax', '(61) 4444-5678', 0, 1);


-- Tenant 17: LibraryBooks
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (17,
        1,
        NOW(),
        'contact@librarybooks.test',
        'LibraryBooks',
        '(71) 3333-5678',
        0,
        1);


# -- Tenant 18: HomeDecor
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (18, 1, NOW(), 'contact@homedecor.test', 'HomeDecor', '(81) 2222-5678', 0, 1);


# -- Tenant 19: TechRepair
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (19, 1, NOW(), 'contact@techrepair.test', 'TechRepair', '(85) 1111-5678', 0, 1);


-- Tenant 20: BakeryCake
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (20, 1, NOW(), 'contact@bakerycake.test', 'BakeryCake', '(11) 9999-9999', 0, 1);


-- Tenant 21: OfficeSupply
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (21,
#         1,
#         NOW(),
#         'contact@officesupply.test',
#         'OfficeSupply',
#         '(21) 8888-9999',
#         0,
#         1);


# -- Tenant 22: JewelryGold
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (22, 1, NOW(), 'contact@jewelrygold.test', 'JewelryGold', '(31) 7777-9999', 0, 1);


# -- Tenant 23: GardenPlants
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (23,
#         1,
#         NOW(),
#         'contact@gardenplants.test',
#         'GardenPlants',
#         '(41) 6666-9999',
#         0,
#         1);


-- Tenant 24: ToyKids
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (24, 1, NOW(), 'contact@toykids.test', 'ToyKids', '(51) 5555-9999', 0, 1);


# -- Tenant 25: MusicStore
# INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
# VALUES (25, 1, NOW(), 'contact@musicstore.test', 'MusicStore', '(61) 4444-9999', 0, 1);


-- Tenant 26: CleanPro
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (26, 1, NOW(), 'contact@cleanpro.test', 'CleanPro', '(71) 3333-9999', 0, 1);


-- Tenant 27: FitnessGym
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (27, 1, NOW(), 'contact@fitnessgym.test', 'FitnessGym', '(81) 2222-9999', 0, 1);


-- Tenant 28: CarWash
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (28, 1, NOW(), 'contact@carwash.test', 'CarWash', '(85) 1111-9999', 0, 1);


-- Tenant 29: PrintShop
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (29, 1, NOW(), 'contact@printshop.test', 'PrintShop', '(11) 9999-0001', 0, 1);


-- Tenant 30: CoffeeLab
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (30, 1, NOW(), 'contact@coffeelab.test', 'CoffeeLab', '(21) 8888-0001', 0, 1);


-- Tenant 31: DrugStore Plus
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (31,
        1,
        NOW(),
        'contact@drugstoreplus.test',
        'DrugStore Plus',
        '(31) 7777-0001',
        0,
        1);


-- Tenant 32: OpticalVision
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (32,
        1,
        NOW(),
        'contact@opticalvision.test',
        'OpticalVision',
        '(41) 6666-0001',
        0,
        1);


-- Tenant 33: FurniturePlus
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (33,
        1,
        NOW(),
        'contact@furnitureplus.test',
        'FurniturePlus',
        '(51) 5555-0001',
        0,
        1);


-- Tenant 34: BabyWorld
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (34, 1, NOW(), 'contact@babyworld.test', 'BabyWorld', '(61) 4444-0001', 0, 1);


-- Tenant 35: BikeStore
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (35, 1, NOW(), 'contact@bikestore.test', 'BikeStore', '(71) 3333-0001', 0, 1);


-- Tenant 36: CameraPhoto
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (36, 1, NOW(), 'contact@cameraphoto.test', 'CameraPhoto', '(81) 2222-0001', 0, 1);


-- Tenant 37: WineSelect
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (37, 1, NOW(), 'contact@wineselect.test', 'WineSelect', '(85) 1111-0001', 0, 1);


-- Tenant 38: FlowerShop
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (38, 1, NOW(), 'contact@flowershop.test', 'FlowerShop', '(11) 9999-0002', 0, 1);


-- Tenant 39: ToolsHardware
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (39,
        1,
        NOW(),
        'contact@toolshardware.test',
        'ToolsHardware',
        '(21) 8888-0002',
        0,
        1);


-- Tenant 40: GlassArt
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (40, 1, NOW(), 'contact@glassart.test', 'GlassArt', '(31) 7777-0002', 0, 1);


-- Tenant 41: DentalCare
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (41, 1, NOW(), 'contact@dentalcare.test', 'DentalCare', '(41) 6666-0002', 0, 1);


-- Tenant 42: PaintSupply
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (42, 1, NOW(), 'contact@paintsupply.test', 'PaintSupply', '(51) 5555-0002', 0, 1);


-- Tenant 43: SafetySecurity
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (43,
        1,
        NOW(),
        'contact@safetysecurity.test',
        'SafetySecurity',
        '(61) 4444-0002',
        0,
        1);


-- Tenant 44: AirCondition
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (44,
        1,
        NOW(),
        'contact@aircondition.test',
        'AirCondition',
        '(71) 3333-0002',
        0,
        1);


-- Tenant 45: PlumbingPro
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (45, 1, NOW(), 'contact@plumbingpro.test', 'PlumbingPro', '(81) 2222-0002', 0, 1);


-- Tenant 46: ElectricWire
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (46,
        1,
        NOW(),
        'contact@electricwire.test',
        'ElectricWire',
        '(85) 1111-0002',
        0,
        1);


-- Tenant 47: PackagingBox
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (47,
        1,
        NOW(),
        'contact@packagingbox.test',
        'PackagingBox',
        '(11) 9999-0003',
        0,
        1);


-- Tenant 48: RecycleGreen
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (48,
        1,
        NOW(),
        'contact@recyclegreen.test',
        'RecycleGreen',
        '(21) 8888-0003',
        0,
        1);


-- Tenant 49: SolarEnergy
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (49, 1, NOW(), 'contact@solarenergy.test', 'SolarEnergy', '(31) 7777-0003', 0, 1);


-- Tenant 50: CloudHost
INSERT IGNORE INTO TB_TNT (ID, ACTIVE, CREATED_AT, EMAIL, NAME, PHONE, TRIAL, PLAN_ID)
VALUES (50, 1, NOW(), 'contact@cloudhost.test', 'CloudHost', '(41) 6666-0003', 0, 1);