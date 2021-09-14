package com.enginious.simplecrypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Localizable;
import org.kohsuke.args4j.Option;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Slf4j
public class SimpleCrypto {

    @Option(name = "-a", aliases = "--action", usage = "action", required = true)
    private String a;
    @Option(name = "-i", aliases = "--input", usage = "input file", required = true)
    private String i;
    @Option(name = "-o", aliases = "--output", usage = "output file", required = true)
    private String o;
    @Option(name = "-k", aliases = "--key", usage = "key used", required = true)
    private String k;

    public static void main(String args[]) throws Exception {
        SimpleCrypto simpleCrypto = new SimpleCrypto();
        CmdLineParser parser = new CmdLineParser(simpleCrypto);
        parser.parseArgument(args);
        simpleCrypto.doWork(parser);
    }

    private void doWork(CmdLineParser parser) throws Exception {

        log.info("input file : {}", i);
        log.info("output file: {}", o);
        log.info("key: {}", k);

        File src = new File(i);
        File out = new File(o);

        switch (StringUtils.lowerCase(a)) {
            case "c":
                log.info("encrypting file {} in {} with key {}", i, o, k);
                encrypt(src, out, k);
                break;
            case "d":
                log.info("decrypting file {} in {} with key {}", i, o, k);
                decrypt(src, out, k);
                break;
            default:
                throw new CmdLineException(parser, new Localizable() {
                    @Override
                    public String formatWithLocale(Locale locale, Object... args) {
                        return "Invalid value for option -a (--action), allowed values are [C] for crypt or [D] for decrypt";
                    }

                    @Override
                    public String format(Object... args) {
                        return "Invalid value for option -a (--action), allowed values are [C] for crypt or [D] for decrypt";
                    }
                });
        }
    }

    private void encrypt(File src, File out, String key) throws Exception {
        doSympleCrypto(Cipher.ENCRYPT_MODE, src, out, key);
    }

    private void decrypt(File src, File out, String key) throws Exception {
        doSympleCrypto(Cipher.DECRYPT_MODE, src, out, key);
    }

    private void doSympleCrypto(int cypherMode, File src, File out, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cypherMode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
        FileUtils.writeByteArrayToFile(out, cipher.doFinal(FileUtils.readFileToByteArray(src)));

    }
}
