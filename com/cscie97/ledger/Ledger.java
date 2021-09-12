package com.cscie97.ledger;
import java.util.*;
import static java.util.Objects.isNull;


/**
 *  ledger
 *
 */
public class Ledger {

    private String name;
    private String description;
    private String seed;
    private TreeMap<Integer, Block> blockMap;
    private Block genesisBlock;

    /**
     * Class Constructor.
     *
     * @param ledgerName name of ledger
     * @param ledgerDescription description of ledger
     * @param ledgerSeed hash value of genesis block (arbitrary)
     */
    public Ledger(String ledgerName, String ledgerDescription, String ledgerSeed){
        this.name = ledgerName;
        this.description = ledgerDescription;
        this.seed = ledgerSeed;
        Account master = new Account("master");
        master.setBalance(2147483647);
        this.blockMap = new TreeMap<Integer, Block>();
        this.genesisBlock = new Block();
        this.genesisBlock.getAccountBalanceMap().put("master", master);
        this.genesisBlock.setPreviousHash(this.seed);
        this.genesisBlock.setBlockNumber(1);
        blockMap.put(1, genesisBlock);
    }

    /**
     * This method creates a new account after verifying that the user selected account address is unique.
     * @param uniqueAddress desired address for new account
     * @return New Account
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    public Account createAccount(String uniqueAddress) throws LedgerException {
        // retrieve last entry in blockchain
        Block currentBlock = blockMap.lastEntry().getValue();
        if (currentBlock.getAccountBalanceMap().containsKey(uniqueAddress)) {
            // if the most recent block contains the address already, require a unique address
            throw new LedgerException("create account", "unique account address required.");
        } else {
            // if valid address, create new account
            HashMap<String, Account> genAcctBalanceMap = currentBlock.getAccountBalanceMap();
            Account newAcct = new Account(uniqueAddress);
            newAcct.setBalance(0);
            // add new account to ledger account balance map
            genAcctBalanceMap.put(uniqueAddress, newAcct);
            return new Account(uniqueAddress);
        }
    };

    /**
     * This method checks the current account balance for the block with a given address.
     * <p>
     * In this ledger implementation, the latest block in the blockchain does not contain 10 transactions,
     * therefore it is not yet "committed". This method queries the block preceding the latest block for
     * accurate account balances.
     *
     * @param address account address
     * @return {@link int}
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    public int getAccountBalance(String address) throws LedgerException {
        // instantiate last entry in blockchain, this entry is yet to be committed
        Block currentBlock = blockMap.lastEntry().getValue();
        try {
            // retrieve the account balance map from block preceding last entry
            // this block has been committed and therefore reflects current account balances
            HashMap<String, Account> acctBalanceMap = currentBlock.getPreviousBlock().getAccountBalanceMap();
            // return account with given address
            Account acct = acctBalanceMap.get(address);
            return acct.getBalance();
        }catch(Exception e){
            // throw exception, invalid address, if no account found
            throw new LedgerException("get account balance", "invalid address");
        }
    }

    /**
     * This method returns a hashmap of all account ids and balances.
     *
     * @return {@link HashMap}
     * @see HashMap
     * @see String
     * @see Integer
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    public HashMap<String, Integer> getAccountBalances() throws LedgerException {
        // create hashmap for return object
        HashMap<String, Integer> accountBalances = new HashMap<String, Integer>();
        try {
            Block lastVerifiedBlock = blockMap.lastEntry().getValue().getPreviousBlock();
            for (var entry : lastVerifiedBlock.getAccountBalanceMap().entrySet()) {
                // iterate through last verified block, appending each balance to return object
                accountBalances.put(entry.getValue().getAddress(), entry.getValue().getBalance());
            }
            return accountBalances;
        }catch(Exception e){
            throw new LedgerException("get all account balances", "no block has been committed");
        }
    }

    /**
     * This method queries the ledger's block map for the transaction with the specified transaction id,
     * it then returns a deep copy of the transaction to insure immutability.
     *
     * @param txId txId
     * @return {@link Transaction}
     * @see Transaction
     */
    public Transaction getTransaction(String txId){
        // create transaction return object
        Transaction retrievedTx = null;
        // parse string input into integer
        int txIdNum = Integer.parseInt(txId);
        for (Map.Entry<Integer, Block> entry : blockMap.entrySet()) {
            // iterate through blocks in ledger find transaction with matching id
            ArrayList<Transaction> transactionList = entry.getValue().getTransactionList();
            for (Transaction tx : transactionList) {
                if (txIdNum == tx.getTransactionId()) {
                    retrievedTx = tx;
                }
            }
        }
        if(isNull(retrievedTx)){
            // if transaction not found, return null
            return null;
        }
        // perform deep copy of transaction to insure immutability
        Transaction copiedTx = new Transaction(Integer.parseInt(txId),
                retrievedTx.getAmount(),
                retrievedTx.getFee(),
                retrievedTx.getPayload(),
                retrievedTx.getPayer(),
                retrievedTx.getReceiver());
        return copiedTx;
    };

    /**
     * This method queries the ledger's block map for the block with the specified block number,
     * it then returns a deep copy of the block to insure immutability.
     *
     * @param blockNum blockNum
     * @return {@link Block}
     * @see Block
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    public Block getBlock(int blockNum) throws LedgerException{
        if (blockNum > blockMap.size() - 1){
            // if block has not yet been committed, throw error
            throw new LedgerException("get block", "block does not exist");
        }
        // retrieve block from blockMap
        Block retrievedBlock = blockMap.get(blockNum);
        // perform deep copy of block to insure immutability
        Block copiedBlock = new Block(blockNum,
                retrievedBlock.getPreviousHash(),
                retrievedBlock.getPreviousBlock());
        copiedBlock.setHash(retrievedBlock.getHash());
        copiedBlock.setTransactionList(retrievedBlock.getTransactionList());
        copiedBlock.setAccountBalanceMap(retrievedBlock.getAccountBalanceMap());
        return copiedBlock;
    };

    /**
     * This method processes a transaction, adds a new block to the ledger's block map if necessary,
     * and returns the new transaction's id.
     * <p>
     * This method verifies:
     * transaction id is unique,
     * receiver and payer accounts exist,
     * payer has sufficient funds,
     * fee is >= 10.
     * Method then adds transaction to current block's transaction list, updates account balances,
     * and calls blockFull() method if block contains 10 transactions.
     *
     * @param tx tx
     * @return {@link String}
     * @see String
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    public String processTransaction(Transaction tx) throws LedgerException {
        String payerAddress = tx.getPayer();
        String receiverAddress = tx.getReceiver();
        int payerBalance;
        int receiverBalance;
        int masterBalance;
        Block currentBlock;

        for (Map.Entry<Integer, Block>
                // enforce unique transaction id
                entry : blockMap.entrySet()) {
            for (Transaction curr : entry.getValue().getTransactionList()) {
                if (tx.getTransactionId() == curr.getTransactionId()){
                    throw new LedgerException("process transaction", "unique transaction id required.");
                }
            }
        }
        if (this.blockMap.size() == 1){
            // if first block in the chain verify accounts exist in genesis block
            try{
                this.genesisBlock.getAccountBalanceMap().get(receiverAddress);
            }catch(Exception e){
                throw new LedgerException("process transaction", "invalid receiver account address.");
            }
            try{
                this.genesisBlock.getAccountBalanceMap().get(payerAddress);
            }catch(Exception e){
                throw new LedgerException("process transaction", "invalid payer account address.");
            }
        }
        else {
            // else use getAccountBalance to check if account exists in Ledger's blockMap
            try{
                getAccountBalance(receiverAddress);
            }catch(Exception e){
                throw new LedgerException("process transaction", "invalid receiver account address.");
            }
            try{
                getAccountBalance(payerAddress);
            }catch(Exception e){
                throw new LedgerException("process transaction", "invalid payer account address.");
            }
        }
        // set current block to last block in blockchain
        currentBlock = blockMap.lastEntry().getValue();

        // instantiate payer and receiver balances
        payerBalance = currentBlock.getAccountBalanceMap().get(payerAddress).getBalance();
        receiverBalance = currentBlock.getAccountBalanceMap().get(receiverAddress).getBalance();

        // instantiate transfer fee and amount variables
        int fee = tx.getFee();
        int amount = tx.getAmount();

        if (payerBalance < (fee + amount)){
            // if payer lacks sufficient funds for transaction, throw error
            throw new LedgerException("process transaction", "payer has insufficient funds.");
        }
        if (fee < 10){
            // if fee is below minimum amount, 10, throw error
            throw new LedgerException("process transaction", "transaction fee must be at least 10.");
        }
        // add transaction to list on latest block in the ledger
        currentBlock.getTransactionList().add(tx);

        // adjust payer balance
        HashMap<String, Account> accountBalances = currentBlock.getAccountBalanceMap();
        accountBalances.get(payerAddress).setBalance(payerBalance - (fee + amount));

        // adjust receiver balance
        accountBalances.get(receiverAddress).setBalance(receiverBalance + amount);

        // adjust master balance
        masterBalance = currentBlock.getAccountBalanceMap().get("master").getBalance();
        accountBalances.get("master").setBalance(masterBalance + fee);

        if(currentBlock.getTransactionList().size() == 10) {
            // if transaction is 10th in block, compute hashes and create new block
            blockFull(currentBlock);
        }
        return Integer.toString(tx.getTransactionId());
    };

    /**
     * This method hashes the current block, creates a new block to receive future transactions,
     * and transfers account balances from current block to new block.
     *
     * @param currentBlock currentBlock
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    private void blockFull(Block currentBlock) throws LedgerException {
        // compute and hash for current block
        String hash = computeHash(currentBlock);
        currentBlock.setHash(hash);

        // increment block id for use in next block
        int nextBlockId = blockMap.size() + 1;

        // create new block to receive next transaction
        // (block id, previous block hash, previous block);
        Block nextBlock = new Block(nextBlockId, hash, currentBlock);

        // add new block to block map in ledger
        blockMap.put(nextBlockId, nextBlock);

        HashMap<String, Integer> previousBalances = getAccountBalances();
        for (var entry : previousBalances.entrySet()) {
            // update balances in newly created block with balances from previous block
            Account temp = createAccount(entry.getKey());
            temp.setBalance(entry.getValue());
            nextBlock.getAccountBalanceMap().put(entry.getKey(), temp);
        }
    }

    /**
     * This method returns a stringified hash value for the given block.
     * This method uses the following formula to compute hash:
     * H(previous_block_hash + H(block_properties + merkle_root))
     *
     * @param currentBlock currentBlock
     * @return {@link String}
     * @see String
     */
    private String computeHash(Block currentBlock){
        ArrayList<String> txList = new ArrayList<String>();
        for (Transaction curr : currentBlock.getTransactionList()) {
            // concatenate all attributes of transactions and add to arraylist
            txList.add(curr.getConcat());
        }
        // compute merkle root with transaction arraylist
        String merkleRoot = MerkleTree.createMerkleTree(txList);
        // compute total hash for current block
        String hash = MerkleTree.getSha(currentBlock.getPreviousHash()
                + MerkleTree.getSha(currentBlock.hashCode() + merkleRoot));
        return hash;
    }

    /**
     * This method validates the state of the blockchain.
     * This method verifies:
     * that account balances total to the max value,
     * each completed block has exactly 10 transactions,
     * the hash of each block is equal to the following block's previousHash field.
     *
     * @throws LedgerException com.cscie97.ledger. ledger exception
     */
    public void validate() throws LedgerException{
        for (Map.Entry<Integer, Block>
                // iterate through each block in the blockchain
                entry : blockMap.entrySet()){
            if (entry.getValue().getBlockNumber() != blockMap.size()){
                // if current block is not the most recent, uncommitted block, perform checks
                if(entry.getValue().getTransactionList().size() != 10){
                    // if block does not contain 10 transactions, throw error
                    throw new LedgerException("validate blockchain", "block does not contain 10 transactions");
                }
                if (entry.getValue().getBlockNumber() > 1) {
                    // if block is not the initial block in chain, perform check
                    if (!(entry.getValue().getPreviousHash()
                            .equals(computeHash(entry.getValue().getPreviousBlock())))) {
                        // retrieve the preceding block, compute the hash
                        // compare hash to the current blocks 'previousHash' attribute
                        throw new LedgerException("validate blockchain", "block hash is not equal to previous block hash," +
                                " blockchain has been manipulated.");
                    }
                }
                int sum = 0;
                for (Map.Entry<String, Account> curr : entry.getValue().getAccountBalanceMap().entrySet()){
                    // add account balances in the current block
                    sum += curr.getValue().getBalance();
                }
                if(sum != Integer.MAX_VALUE){
                    // if sum of balances is not equal to original master balance, throw error
                    throw new LedgerException("validate blockchain", "account balances do not total to" +
                            " the initial total blockchain value");
                }
            }
        }
    }


    /**
     * get name
     *
     * @return {@link String}
     * @see String
     */
    public String getName() {return this.name;}


    /**
     * set name
     *
     * @param name name
     */
    public void setName(String name) {this.name = name;}


    /**
     * get description
     *
     * @return {@link String}
     * @see String
     */
    public String getDescription() {return this.description;}


    /**
     * set description
     *
     * @param description description
     */
    public void setDescription(String description) {this.description = description;}


    /**
     * get seed
     *
     * @return {@link String}
     * @see String
     */
    public String getSeed() {return this.seed;}


    /**
     * set seed
     *
     * @param seed seed
     */
    public void setSeed(String seed) {this.seed = seed;}


    /**
     * get block map
     *
     * @return {@link TreeMap}
     * @see TreeMap
     * @see Integer
     * @see Block
     */
    public TreeMap<Integer, Block> getBlockMap() {return this.blockMap;}


    /**
     * set block map
     *
     * @param blockMap blockMap
     */
    public void setBlockMap(TreeMap<Integer, Block> blockMap) {this.blockMap = blockMap;}


    /**
     * get genesis block
     *
     * @return {@link Block}
     * @see Block
     */
    public Block getGenesisBlock() {return this.genesisBlock;}


    /**
     * set genesis block
     *
     * @param genesisBlock genesisBlock
     */
    public void setGenesisBlock(Block genesisBlock) {this.genesisBlock = genesisBlock;}
}
