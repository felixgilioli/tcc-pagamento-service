CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabela de pagamento
CREATE TABLE IF NOT EXISTS pagamento (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pedido_id VARCHAR(150) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    status VARCHAR(50) NOT NULL,
    link VARCHAR(255) NOT NULL
);