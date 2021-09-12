package com.cscie97.ledger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 *  block
 *
 */
public class Block {

    private int blockNumber;
    private String previousHash;
    private String hash;
    private HashMap<String, Account> accountBalanceMap;
    private ArrayList<Transaction> transactionList;
    private Block previousBlock;

    /**
     * Class constructor used to create genesis block
     */
    public Block() {
        this.blockNumber = 0;
        this.previousHash = null;
        this.hash = null;
        this.accountBalanceMap = new HashMap<String, Account>();
        this.transactionList = new ArrayList<Transaction>();
        this.previousBlock = null;
    }

    /**
     * Class constructor used for each new block after genesis block
     *
     * @param blockNumber
     * @param previousHash
     * @param previousBlock
     */
    public Block(int blockNumber, String previousHash, Block previousBlock){
        this.blockNumber = blockNumber;
        this.previousHash = previousHash;
        this.previousBlock = previousBlock;
        this.transactionList = new ArrayList<Transaction>();
        this.hash = "";
        this.accountBalanceMap = new HashMap<String, Account>();
    }

    /**
     * to string method
     *
     * @return {@link String}
     * @see String
     */
    @Override
    public String toString() {
        String blockInfo;
        if (previousBlock != null){
            blockInfo = "\nPrevious Block Number: " + previousBlock.getBlockNumber();
        }else{
            blockInfo = "\nPrevious Block: is null";
        }
        return "Block Number:  " + blockNumber +
                "\nPrevious Hash: " + previousHash +
                "\nHash: " + hash + blockInfo;
    }

    /**
     * hash code method
     *
     * @return {@link int}
     */
    @Override
    public int hashCode() {
        return Objects.hash(blockNumber, previousHash, accountBalanceMap, transactionList, previousBlock);
    }

    /**
     * get block number
     *
     * @return {@link int}
     */
    public int getBlockNumber() {
        return this.blockNumber;
    }


    /**
     * set block number
     *
     * @param blockNumber blockNumber
     */
    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }


    /**
     * get previous hash
     *
     * @return {@link String}
     * @see String
     */
    public String getPreviousHash() {
        return this.previousHash;
    }


    /**
     * set previous hash
     *
     * @param previousHash previousHash
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }


    /**
     * get hash
     *
     * @return {@link String}
     * @see String
     */
    public String getHash() {
        return this.hash;
    }


    /**
     * set hash
     *
     * @param hash hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }


    /**
     * get account balance map
     *
     * @return {@link HashMap}
     * @see HashMap
     * @see String
     * @see Account
     */
    public HashMap<String, Account> getAccountBalanceMap() {
        return this.accountBalanceMap;
    }


    /**
     * set account balance map
     *
     * @param accountBalanceMap accountBalanceMap
     */
    public void setAccountBalanceMap(HashMap<String, Account> accountBalanceMap) {
        this.accountBalanceMap = accountBalanceMap;
    }


    /**
     * get transaction list
     *
     * @return {@link ArrayList}
     * @see ArrayList
     * @see Transaction
     */
    public ArrayList<Transaction> getTransactionList() {
        return this.transactionList;
    }


    /**
     * set transaction list
     *
     * @param transactionList transactionList
     */
    public void setTransactionList(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }


    /**
     * get previous block
     *
     * @return {@link Block}
     * @see Block
     */
    public Block getPreviousBlock() {
        return this.previousBlock;
    }


    /**
     * set previous block
     *
     * @param previousBlock previousBlock
     */
    public void setPreviousBlock(Block previousBlock) {
        this.previousBlock = previousBlock;
    }
}