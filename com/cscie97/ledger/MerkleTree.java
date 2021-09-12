package com.cscie97.ledger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 *  merkle tree
 *
 */
public class MerkleTree {

    /**
     * Helper function for the recursive merkleTree() method.
     * Implementation by Vinay Prabhu
     * https://gist.github.com/Vinayprabhu19/3546a9f742bb74d2e0682877fec4ee86#file-merkeltree-java
     *
     * @param txnLists txnLists
     * @return {@link String}
     * @see String
     */
    public static String createMerkleTree(ArrayList<String> txnLists) {
        ArrayList<String> merkleRoot = merkleTree(txnLists);
        return merkleRoot.get(0);
    }

    /**
     * Recursively creates a merkle tree, hashes values using getSha(), and returns the merkle root.
     * Implementation by Vinay Prabhu
     * https://gist.github.com/Vinayprabhu19/3546a9f742bb74d2e0682877fec4ee86#file-merkeltree-java
     *
     * @param hashList hashList
     * @return {@link ArrayList}
     * @see ArrayList
     * @see String
     */
    private static ArrayList<String> merkleTree(ArrayList<String> hashList){
        // Return the Merkle Root
        if(hashList.size() == 1){
            return hashList;
        }
        ArrayList<String> parentHashList=new ArrayList<>();
        // hash the leaf transaction pair to get parent transaction
        for(int i=0; i<hashList.size(); i+=2){
            // if odd number of transactions, add the last transaction again
            if((hashList.size() % 2 == 1) && (i == hashList.size() - 1)) {
                String lastHash = hashList.get(hashList.size() - 1);
                String hashedString = getSha(lastHash.concat(lastHash));
                parentHashList.add(hashedString);
                break;
            }else {
                String hashedString = getSha(hashList.get(i).concat(hashList.get(i + 1)));
                parentHashList.add(hashedString);
            }
        }
        return merkleTree(parentHashList);
    }

    /**
     * Takes a string as a parameter, returns a SHA256 hash of the string.
     * Implementation by Vinay Prabhu
     * https://gist.github.com/Vinayprabhu19/3546a9f742bb74d2e0682877fec4ee86#file-merkeltree-java
     *
     * @param input input
     * @return {@link String}
     * @see String
     */
    public static String getSha(String input){
        //String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(concat);
        {
            try {
                // static getInstance method is called with hashing SHA
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // digest() method called
                // to calculate message digest of an input
                // and return array of byte
                byte[] messageDigest = md.digest(input.getBytes());

                // convert byte array into signum representation
                BigInteger no = new BigInteger(1, messageDigest);

                // convert message digest into hex value
                String hashText = no.toString(16);
                while (hashText.length() < 32) {
                    hashText = "0" + hashText;
                }
                return hashText;
            }
            // for specifying wrong message digest algorithms
            catch (NoSuchAlgorithmException e) {
                System.out.println("Exception thrown"
                        + " for incorrect algorithm: " + e);
                return null;
            }
        }
    }
}
