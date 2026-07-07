DROP DATABASE IF EXISTS ApoioDigitalDB;
CREATE DATABASE ApoioDigitalDB;
USE ApoioDigitalDB;

CREATE TABLE AppSuportado (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    nome varchar(255),
    descricao varchar(255),
    referencia varchar(255),
    situacao varchar(255),
    pacote varchar(255)
);

CREATE TABLE Cliente (
    id varchar(36) PRIMARY KEY,
    nome varchar(255),
    access_key varchar(255) UNIQUE,
    area_atuacao varchar(255)
);

CREATE TABLE Usuario (
    id varchar(36) PRIMARY KEY,
    nome varchar(255),
    id_cliente varchar(36),
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id)
);

CREATE TABLE Personalizacao (
    id varchar(36) PRIMARY KEY,
    id_cliente varchar(36),
    regra_personalizada TEXT,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id)
);

CREATE TABLE Pedido (
    id varchar(36) PRIMARY KEY,
    id_usuario varchar(36),
    prompt TEXT,
    timestamp DATETIME,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id)
);

CREATE TABLE Resposta (
    id varchar(36) PRIMARY KEY,
    id_pedido varchar(36),
    mensagem TEXT,
    raciocinio TEXT,
    timestamp DATETIME,
    FOREIGN KEY (id_pedido) REFERENCES Pedido(id)
);

CREATE TABLE Componente (
    id varchar(36) PRIMARY KEY,
    id_resposta varchar(36),
    assinatura TEXT,
    FOREIGN KEY (id_resposta) REFERENCES Resposta(id)
);


INSERT INTO AppSuportado (id, nome, descricao, referencia, situacao, pacote) VALUES
(1,'WhatsApp', 'Aplicativo de mensagens instantâneas', 'mensagem', 'Utilizado para se comunicar', 'com.whatsapp'),
(2,'Instagram', 'Rede social para fotos e vídeos curtos', 'rede social', 'Usado para compartilhar imagens', 'com.instagram.android'),
(3,'Facebook', 'Rede social para amigos e comunidades', 'rede social', 'Usado para interações e grupos', 'com.facebook.katana'),
(4,'TikTok', 'App de vídeos curtos e tendências', 'vídeos', 'Usado para entretenimento', 'com.zhiliaoapp.musically'),
(5,'Twitter', 'Rede social de postagens curtas', 'texto', 'Usado para compartilhar ideias', 'com.twitter.android'),
(6,'Telegram', 'Mensageiro rápido e com canais', 'mensagem', 'Usado para conversar e seguir canais', 'org.telegram.messenger'),
(7,'YouTube', 'Plataforma de vídeos online', 'vídeos', 'Usado para assistir conteúdo', 'com.google.android.youtube'),
(8,'Spotify', 'Streaming de música e podcasts', 'música', 'Usado para ouvir músicas', 'com.spotify.music'),
(9,'Netflix', 'Streaming de filmes e séries', 'filmes', 'Usado para assistir entretenimento', 'com.netflix.mediaclient'),
(10,'Prime Video', 'Streaming da Amazon', 'filmes', 'Usado para assistir séries e filmes', 'com.amazon.avod.thirdpartyclient'),
(11,'Disney+', 'Streaming de produções da Disney', 'filmes', 'Usado para assistir conteúdo familiar', 'com.disney.disneyplus'),
(12,'iFood', 'Entrega de comida e mercado', 'comida', 'Usado para pedir refeições', 'br.com.brainweb.ifood'),
(13,'Rappi', 'Entrega de produtos e comida', 'delivery', 'Usado para pedir compras e refeições', 'com.grability.rappi'),
(15,'Uber', 'Transporte particular e delivery', 'transporte', 'Usado para solicitar corridas', 'com.ubercab'),
(16,'99', 'Aplicativo de transporte urbano', 'transporte', 'Usado para corridas rápidas', 'com.taxis99'),
(17,'Mercado Livre', 'Plataforma de compras online', 'compras', 'Usado para comprar produtos', 'com.mercadolibre'),
(18,'Shopee', 'Marketplace de produtos variados', 'compras', 'Usado para comprar com descontos', 'com.shopee.br'),
(19,'Amazon', 'Loja online e entrega rápida', 'compras', 'Usado para comprar produtos online', 'com.amazon.mShop.android.shopping'),
(20,'Magazine Luiza', 'Marketplace brasileiro', 'compras', 'Usado para adquirir eletrônicos e móveis', 'com.luizalabs.mlapp'),
(21,'Nubank', 'Banco digital com cartão de crédito', 'banco', 'Usado para gerenciar finanças', 'com.nu.production'),
(22,'Banco Inter', 'Banco digital completo', 'banco', 'Usado para contas e investimentos', 'br.com.intermedium'),
(23,'Itaú', 'Banco tradicional com app moderno', 'banco', 'Usado para transações financeiras', 'com.itau'),
(24,'Bradesco', 'Banco com app para clientes', 'banco', 'Usado para pagar e transferir valores', 'com.bradesco'),
(25,'Caixa', 'Aplicativo da Caixa Econômica', 'banco', 'Usado para benefícios e serviços', 'br.com.gabba.Caixa'),
(26,'PicPay', 'Carteira digital de pagamentos', 'pagamento', 'Usado para pagar com QR Code', 'com.picpay'),
(27,'Mercado Pago', 'Serviço de pagamentos digitais', 'pagamento', 'Usado para enviar e receber dinheiro', 'com.mercadopago.wallet'),
(28,'PagBank', 'Conta digital do PagSeguro', 'banco', 'Usado para transferências e pagamentos', 'br.com.uol.ps.myaccount'),
(29,'Google Maps', 'Navegação e rotas GPS', 'mapa', 'Usado para localizar endereços', 'com.google.android.apps.maps'),
(30,'Waze', 'App de navegação com trânsito', 'mapa', 'Usado para dirigir com rotas rápidas', 'com.waze'),
(31,'Google Chrome', 'Navegador web rápido e seguro', 'navegador', 'Usado para acessar a internet', 'com.android.chrome'),
(32,'Firefox', 'Navegador de código aberto', 'navegador', 'Usado para navegar na web', 'org.mozilla.firefox'),
(33,'Gmail', 'Serviço de e-mail da Google', 'email', 'Usado para enviar mensagens eletrônicas', 'com.google.android.gm'),
(34,'Outlook', 'Serviço de e-mail da Microsoft', 'email', 'Usado para comunicação corporativa', 'com.microsoft.office.outlook'),
(42,'Google Drive', 'Armazenamento em nuvem', 'nuvem', 'Usado para salvar arquivos', 'com.google.android.apps.docs'),
(49,'Duolingo', 'Aprendizado de idiomas gamificado', 'educação', 'Usado para aprender línguas', 'com.duolingo'),
(50,'Kwai', 'App de vídeos curtos e lives', 'vídeos', 'Usado para assistir e criar vídeos', 'com.kwai.video')
ON DUPLICATE KEY UPDATE
	pacote = VALUES(pacote);
