-- ==================================================================================
-- MIGRATION: Insert_50_Tenants.sql
-- DESCRIÇÃO: Insere 50 tenants de teste na master database
-- ==================================================================================
-- Tenant 1: HECE Distribuidora
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        1,
        'HECE Distribuidora',
        'contact@hece.test',
        '(11) 9999-8888',
        TRUE,
        '73648954000189',
        'HECE Distribuidora LTDA',
        'HECE',
        'LUCRO_PRESUMIDO',
        'HECE'
    );

-- Tenant 2: Tech Solutions
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        2,
        'Tech Solutions',
        'contact@techsolutions.test',
        '(11) 8888-7777',
        TRUE,
        '15645509000169',
        'Tech Solutions Brazil LTDA',
        'Tech Solutions',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 3: Logística Global
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        3,
        'Logística Global',
        'contact@logistica.test',
        '(11) 7777-6666',
        TRUE,
        '04664133000113',
        'Logística Global Express LTDA',
        'Logística Global',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 4: Varejo Express
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        4,
        'Varejo Express',
        'contact@varejo.test',
        '(11) 6666-5555',
        TRUE,
        '77841474000190',
        'Varejo Express Distribution LTDA',
        'Varejo Express',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 5: FarmaSaúde
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        5,
        'FarmaSaúde',
        'contact@farmasaude.test',
        '(21) 3456-7890',
        TRUE,
        '04773222000106',
        'FarmaSaúde Medicamentos LTDA',
        'FarmaSaúde',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 6: AutoPeças Brasil
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        6,
        'AutoPeças Brasil',
        'contact@autopecas.test',
        '(11) 2345-6789',
        TRUE,
        '91566255000171',
        'AutoPeças Brasil Comércio LTDA',
        'AutoPeças Brasil',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 7: Construmax
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        7,
        'Construmax',
        'contact@construmax.test',
        '(41) 3333-4444',
        TRUE,
        '28301898000157',
        'Construmax Materiais de Construção LTDA',
        'Construmax',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 8: EletroTech
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        8,
        'EletroTech',
        'contact@eletrotech.test',
        '(51) 4444-5555',
        TRUE,
        '65385804000100',
        'EletroTech Eletrônicos LTDA',
        'EletroTech',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 9: Alimentos Naturais
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        9,
        'Alimentos Naturais',
        'contact@alimentosnaturais.test',
        '(85) 5555-6666',
        TRUE,
        '32625364000153',
        'Alimentos Naturais Indústria LTDA',
        'Alimentos Naturais',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 10: ModaStyle
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        10,
        'ModaStyle',
        'contact@modastyle.test',
        '(11) 5555-7777',
        TRUE,
        '62531023000180',
        'ModaStyle Confecções LTDA',
        'ModaStyle',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 11: InfoSystems
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        11,
        'InfoSystems',
        'contact@infosystems.test',
        '(11) 7788-9900',
        TRUE,
        '86208662000177',
        'InfoSystems Tecnologia LTDA',
        'InfoSystems',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 12: GreenAgro
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        12,
        'GreenAgro',
        'contact@greenagro.test',
        '(16) 8899-0011',
        TRUE,
        '92659978000188',
        'GreenAgro Products Agrícolas LTDA',
        'GreenAgro',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 13: MegaAtacado
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        13,
        'MegaAtacado',
        'contact@megaatacado.test',
        '(19) 9900-1122',
        TRUE,
        '45757493000147',
        'MegaAtacado Distribuição LTDA',
        'MegaAtacado',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 14: PetShop Amigo
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        14,
        'PetShop Amigo',
        'contact@petshopamiго.test',
        '(21) 1122-3344',
        TRUE,
        '04739513000170',
        'PetShop Amigo Comércio LTDA',
        'PetShop Amigo',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 15: BeautyCenter
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        15,
        'BeautyCenter',
        'contact@beautycenter.test',
        '(31) 2233-4455',
        TRUE,
        '69994466000156',
        'BeautyCenter Cosméticos LTDA',
        'BeautyCenter',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 16: SportMax
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        16,
        'SportMax',
        'contact@sportmax.test',
        '(48) 3344-5566',
        TRUE,
        '09233339000102',
        'SportMax Artigos Esportivos LTDA',
        'SportMax',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 17: LibraryBooks
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        17,
        'LibraryBooks',
        'contact@librarybooks.test',
        '(61) 4455-6677',
        TRUE,
        '85681464000163',
        'LibraryBooks Livraria LTDA',
        'LibraryBooks',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 18: HomeDecor
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        18,
        'HomeDecor',
        'contact@homedecor.test',
        '(71) 5566-7788',
        TRUE,
        '73784995000100',
        'HomeDecor Decorações LTDA',
        'HomeDecor',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 19: TechRepair
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        19,
        'TechRepair',
        'contact@techrepair.test',
        '(81) 6677-8899',
        TRUE,
        '96059153000183',
        'TechRepair Assistência Técnica LTDA',
        'TechRepair',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 20: BakeryCake
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        20,
        'BakeryCake',
        'contact@bakerycake.test',
        '(11) 7788-9900',
        TRUE,
        '48510483000109',
        'BakeryCake Panificadora LTDA',
        'BakeryCake',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 21: OfficeSupply
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        21,
        'OfficeSupply',
        'contact@officesupply.test',
        '(27) 8899-0011',
        TRUE,
        '57899977000100',
        'OfficeSupply Papelaria LTDA',
        'OfficeSupply',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 22: JewelryGold
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        22,
        'JewelryGold',
        'contact@jewelrygold.test',
        '(47) 9900-1122',
        TRUE,
        '34851082000108',
        'JewelryGold Joalheria LTDA',
        'JewelryGold',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 23: GardenPlants
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        23,
        'GardenPlants',
        'contact@gardenplants.test',
        '(62) 1122-3344',
        TRUE,
        '70553134000115',
        'GardenPlants Jardinagem LTDA',
        'GardenPlants',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 24: ToyKids
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        24,
        'ToyKids',
        'contact@toykids.test',
        '(11) 2233-4455',
        TRUE,
        '65562657000198',
        'ToyKids Brinquedos LTDA',
        'ToyKids',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 25: MusicStore
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        25,
        'MusicStore',
        'contact@musicstore.test',
        '(21) 3344-5566',
        TRUE,
        '95390896000179',
        'MusicStore Instrumentos Musicais LTDA',
        'MusicStore',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 26: CleanPro
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        26,
        'CleanPro',
        'contact@cleanpro.test',
        '(31) 4455-6677',
        TRUE,
        '42192323000174',
        'CleanPro Products de Limpeza LTDA',
        'CleanPro',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 27: FitnessGym
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        27,
        'FitnessGym',
        'contact@fitnessgym.test',
        '(41) 5566-7788',
        TRUE,
        '10254401000121',
        'FitnessGym Academia LTDA',
        'FitnessGym',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 28: CarWash
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        28,
        'CarWash',
        'contact@carwash.test',
        '(51) 6677-8899',
        TRUE,
        '78478959000124',
        'CarWash Lava Rápido LTDA',
        'CarWash',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 29: PrintShop
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        29,
        'PrintShop',
        'contact@printshop.test',
        '(61) 7788-9900',
        TRUE,
        '27495673000116',
        'PrintShop Gráfica Rápida LTDA',
        'PrintShop',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 30: CoffeeLab
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        30,
        'CoffeeLab',
        'contact@coffeelab.test',
        '(71) 8899-0011',
        TRUE,
        '83095821000186',
        'CoffeeLab Cafeteria LTDA',
        'CoffeeLab',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 31: DrugStore Plus
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        31,
        'DrugStore Plus',
        'contact@drugstoreplus.test',
        '(81) 9900-1122',
        TRUE,
        '34815588000153',
        'DrugStore Plus Drogaria LTDA',
        'DrugStore Plus',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 32: OpticalVision
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        32,
        'OpticalVision',
        'contact@opticalvision.test',
        '(85) 1111-2222',
        TRUE,
        '98544384000108',
        'OpticalVision Ótica LTDA',
        'OpticalVision',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 33: FurniturePlus
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        33,
        'FurniturePlus',
        'contact@furnitureplus.test',
        '(11) 2222-3333',
        TRUE,
        '98544384000108',
        'FurniturePlus Móveis LTDA',
        'FurniturePlus',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 34: BabyWorld
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        34,
        'BabyWorld',
        'contact@babyworld.test',
        '(21) 3333-4444',
        TRUE,
        '41216181000175',
        'BabyWorld Artigos Infantis LTDA',
        'BabyWorld',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 35: BikeStore
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        35,
        'BikeStore',
        'contact@bikestore.test',
        '(31) 4444-5555',
        TRUE,
        '48902230000172',
        'BikeStore Bicicletas LTDA',
        'BikeStore',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 36: CameraPhoto
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        36,
        'CameraPhoto',
        'contact@cameraphoto.test',
        '(41) 5555-6666',
        TRUE,
        '08175562000188',
        'CameraPhoto Fotografia LTDA',
        'CameraPhoto',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 37: WineSelect
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        37,
        'WineSelect',
        'contact@wineselect.test',
        '(51) 6666-7777',
        TRUE,
        '38859381000131',
        'WineSelect Adega LTDA',
        'WineSelect',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 38: FlowerShop
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        38,
        'FlowerShop',
        'contact@flowershop.test',
        '(61) 7777-8888',
        TRUE,
        '68519464000142',
        'FlowerShop Floricultura LTDA',
        'FlowerShop',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 39: ToolsHardware
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        39,
        'ToolsHardware',
        'contact@toolshardware.test',
        '(71) 8888-9999',
        TRUE,
        '68278857000101',
        'ToolsHardware Ferramentas LTDA',
        'ToolsHardware',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 40: GlassArt
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        40,
        'GlassArt',
        'contact@glassart.test',
        '(81) 9999-0000',
        TRUE,
        '48610629000180',
        'GlassArt Vidraçaria LTDA',
        'GlassArt',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 41: DentalCare
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        41,
        'DentalCare',
        'contact@dentalcare.test',
        '(85) 1234-5678',
        TRUE,
        '71182135000163',
        'DentalCare Odontologia LTDA',
        'DentalCare',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 42: PaintSupply
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        42,
        'PaintSupply',
        'contact@paintsupply.test',
        '(11) 5678-9012',
        TRUE,
        '53445729000138',
        'PaintSupply Tintas LTDA',
        'PaintSupply',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 43: SafetySecurity
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        43,
        'SafetySecurity',
        'contact@safetysecurity.test',
        '(21) 6789-0123',
        TRUE,
        '62412792000169',
        'SafetySecurity Segurança LTDA',
        'SafetySecurity',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 44: AirCondition
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        44,
        'AirCondition',
        'contact@aircondition.test',
        '(31) 7890-1234',
        TRUE,
        '16278636000130',
        'AirCondition Climatização LTDA',
        'AirCondition',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 45: PlumbingPro
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        45,
        'PlumbingPro',
        'contact@plumbingpro.test',
        '(41) 8901-2345',
        TRUE,
        '64075877000124',
        'PlumbingPro Hidráulica LTDA',
        'PlumbingPro',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 46: ElectricWire
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        46,
        'ElectricWire',
        'contact@electricwire.test',
        '(51) 9012-3456',
        TRUE,
        '77465974000175',
        'ElectricWire Elétrica LTDA',
        'ElectricWire',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 47: PackagingBox
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        47,
        'PackagingBox',
        'contact@packagingbox.test',
        '(61) 1234-5679',
        TRUE,
        '86473253000106',
        'PackagingBox Embalagens LTDA',
        'PackagingBox',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );

-- Tenant 48: RecycleGreen
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        48,
        'RecycleGreen',
        'contact@recyclegreen.test',
        '(71) 2345-6780',
        TRUE,
        '01079545000106',
        'RecycleGreen Reciclagem LTDA',
        'RecycleGreen',
        'LUCRO_REAL',
        'DEFAULT'
    );

-- Tenant 49: SolarEnergy
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        49,
        'SolarEnergy',
        'contact@solarenergy.test',
        '(81) 3456-7801',
        TRUE,
        '92564212000110',
        'SolarEnergy Energia Solar LTDA',
        'SolarEnergy',
        'LUCRO_PRESUMIDO',
        'DEFAULT'
    );

-- Tenant 50: CloudHost
INSERT IGNORE INTO tb_tenant (
    id,
    nome,
    email,
    telefone,
    ativa,
    dados_fiscais_cnpj,
    dados_fiscais_razao_social,
    dados_fiscais_nome_fantasia,
    dados_fiscais_regime_tributario,
    TENANT_TYPE
)
VALUES
    (
        50,
        'CloudHost',
        'contact@cloudhost.test',
        '(85) 4567-8902',
        TRUE,
        '19967264000147',
        'CloudHost Hospedagem LTDA',
        'CloudHost',
        'SIMPLES_NACIONAL',
        'DEFAULT'
    );