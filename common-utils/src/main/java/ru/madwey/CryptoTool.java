package ru.madwey;

import org.hashids.Hashids;

public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool(String salt) {
        var minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    //Шифрование id
    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    //Дешифрование id
    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        return res[0];
    }
}
