CREATE SEQUENCE IF NOT EXISTS SQ_INTERNAL_TRANSFER_IDT;

CREATE TABLE IF NOT EXISTS INTERNAL_TRANSFER (
	IDT_INTERNAL_TRANSFER BIGINT DEFAULT SQ_INTERNAL_TRANSFER_IDT.NEXTVAL,
   	COD_OPERATION VARCHAR(32) NOT NULL,

	COD_ACCOUNT_CREDITOR VARCHAR(32) NOT NULL,
	NUM_CREDITOR_ACCOUNT_AGENCY INT NOT NULL,
	NUM_CREDITOR_ACCOUNT_NUMBER INT NOT NULL,

	COD_ACCOUNT_DEBTOR VARCHAR(32) NOT NULL,
    NUM_DEBTOR_ACCOUNT_AGENCY INT NOT NULL,
    NUM_DEBTOR_ACCOUNT_NUMBER INT NOT NULL,

	DAT_CREATION TIMESTAMP NOT NULL,

	NUM_VALUE DOUBLE PRECISION NOT NULL,

    FLG_BACEN_SINC BOOLEAN NOT NULL,
    DAT_LAST_BACEN_SINC_ATTEMPT TIMESTAMP,
	PRIMARY KEY (IDT_INTERNAL_TRANSFER)
);

CREATE UNIQUE INDEX IF NOT EXISTS INTERNAL_TRANSFER_UK01 ON INTERNAL_TRANSFER (COD_OPERATION);

