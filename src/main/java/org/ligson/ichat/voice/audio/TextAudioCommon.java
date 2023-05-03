package org.ligson.ichat.voice.audio;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class TextAudioCommon {

    public static String unescape(String encode) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByExtension("js");
        //解码后url
        String unUrl = null;
        try {
            unUrl = (String) engine.eval("unescape('" + encode + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return unUrl;
    }

    public static String encodeURIComponent(String x) {
        return URLEncoder.encode(x, Charset.defaultCharset());
    }

    public static String getDataSign(String s) {
        int[] fix = new int[]{0x67452301, -0x10325477, -0x67452302, 0x10325476};
        int length = s.length();
        int index;
        for (index = 0x40; index <= length; index += 0x40) {
            int[] ints = toInts(s.substring(index - 0x40, index));
            mergeInts(fix, ints);
        }

        int llength = (s = s.substring(index - 0x40)).length();
        int[] res2 = new int[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
        for (index = 0x0; index < llength; index += 0x1) {
            res2[index >> 0x2] |= Character.codePointAt(s, index) << (index % 0x4 << 0x3);
        }
        res2[index >> 0x2] |= 0x80 << (index % 0x4 << 0x3);
        if (index > 0x37) {
            mergeInts(fix, res2);
            for (index = 0x0; index < 0x10; index += 0x1) {
                res2[index] = 0x0;
            }
        }
        System.out.println();
        int size = length * 8;
        res2[0xe] = size;
        res2[0xf] = 0;
        mergeInts(fix, res2);

        return getJoin(fix);
    }

    public static void mergeInts(int[] fix, int[] ints) {
        int fix0 = fix[0x0], fix1 = fix[0x1], fix2 = fix[0x2], fix3 = fix[0x3];
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix2 | ~fix1 & fix3) + ints[0x0] - 0x28955b88 | 0x0) << 0x7 | fix0 >>> 0x19) + fix1 | 0x0) & fix1 | ~fix0 & fix2) + ints[0x1] - 0x173848aa | 0x0) << 0xc | fix3 >>> 0x14) + fix0 | 0x0) & fix0 | ~fix3 & fix1) + ints[0x2] + 0x242070db | 0x0) << 0x11 | fix2 >>> 0xf) + fix3 | 0x0) & fix3 | ~fix2 & fix0) + ints[0x3] - 0x3e423112 | 0x0) << 0x16 | fix1 >>> 0xa) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix2 | ~fix1 & fix3) + ints[0x4] - 0xa83f051 | 0x0) << 0x7 | fix0 >>> 0x19) + fix1 | 0x0) & fix1 | ~fix0 & fix2) + ints[0x5] + 0x4787c62a | 0x0) << 0xc | fix3 >>> 0x14) + fix0 | 0x0) & fix0 | ~fix3 & fix1) + ints[0x6] - 0x57cfb9ed | 0x0) << 0x11 | fix2 >>> 0xf) + fix3 | 0x0) & fix3 | ~fix2 & fix0) + ints[0x7] - 0x2b96aff | 0x0) << 0x16 | fix1 >>> 0xa) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix2 | ~fix1 & fix3) + ints[0x8] + 0x698098d8 | 0x0) << 0x7 | fix0 >>> 0x19) + fix1 | 0x0) & fix1 | ~fix0 & fix2) + ints[0x9] - 0x74bb0851 | 0x0) << 0xc | fix3 >>> 0x14) + fix0 | 0x0) & fix0 | ~fix3 & fix1) + ints[0xa] - 0xa44f | 0x0) << 0x11 | fix2 >>> 0xf) + fix3 | 0x0) & fix3 | ~fix2 & fix0) + ints[0xb] - 0x76a32842 | 0x0) << 0x16 | fix1 >>> 0xa) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix2 | ~fix1 & fix3) + ints[0xc] + 0x6b901122 | 0x0) << 0x7 | fix0 >>> 0x19) + fix1 | 0x0) & fix1 | ~fix0 & fix2) + ints[0xd] - 0x2678e6d | 0x0) << 0xc | fix3 >>> 0x14) + fix0 | 0x0) & fix0 | ~fix3 & fix1) + ints[0xe] - 0x5986bc72 | 0x0) << 0x11 | fix2 >>> 0xf) + fix3 | 0x0) & fix3 | ~fix2 & fix0) + ints[0xf] + 0x49b40821 | 0x0) << 0x16 | fix1 >>> 0xa) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix3 | fix2 & ~fix3) + ints[0x1] - 0x9e1da9e | 0x0) << 0x5 | fix0 >>> 0x1b) + fix1 | 0x0) & fix2 | fix1 & ~fix2) + ints[0x6] - 0x3fbf4cc0 | 0x0) << 0x9 | fix3 >>> 0x17) + fix0 | 0x0) & fix1 | fix0 & ~fix1) + ints[0xb] + 0x265e5a51 | 0x0) << 0xe | fix2 >>> 0x12) + fix3 | 0x0) & fix0 | fix3 & ~fix0) + ints[0x0] - 0x16493856 | 0x0) << 0x14 | fix1 >>> 0xc) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix3 | fix2 & ~fix3) + ints[0x5] - 0x29d0efa3 | 0x0) << 0x5 | fix0 >>> 0x1b) + fix1 | 0x0) & fix2 | fix1 & ~fix2) + ints[0xa] + 0x2441453 | 0x0) << 0x9 | fix3 >>> 0x17) + fix0 | 0x0) & fix1 | fix0 & ~fix1) + ints[0xf] - 0x275e197f | 0x0) << 0xe | fix2 >>> 0x12) + fix3 | 0x0) & fix0 | fix3 & ~fix0) + ints[0x4] - 0x182c0438 | 0x0) << 0x14 | fix1 >>> 0xc) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix3 | fix2 & ~fix3) + ints[0x9] + 0x21e1cde6 | 0x0) << 0x5 | fix0 >>> 0x1b) + fix1 | 0x0) & fix2 | fix1 & ~fix2) + ints[0xe] - 0x3cc8f82a | 0x0) << 0x9 | fix3 >>> 0x17) + fix0 | 0x0) & fix1 | fix0 & ~fix1) + ints[0x3] - 0xb2af279 | 0x0) << 0xe | fix2 >>> 0x12) + fix3 | 0x0) & fix0 | fix3 & ~fix0) + ints[0x8] + 0x455a14ed | 0x0) << 0x14 | fix1 >>> 0xc) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 & fix3 | fix2 & ~fix3) + ints[0xd] - 0x561c16fb | 0x0) << 0x5 | fix0 >>> 0x1b) + fix1 | 0x0) & fix2 | fix1 & ~fix2) + ints[0x2] - 0x3105c08 | 0x0) << 0x9 | fix3 >>> 0x17) + fix0 | 0x0) & fix1 | fix0 & ~fix1) + ints[0x7] + 0x676f02d9 | 0x0) << 0xe | fix2 >>> 0x12) + fix3 | 0x0) & fix0 | fix3 & ~fix0) + ints[0xc] - 0x72d5b376 | 0x0) << 0x14 | fix1 >>> 0xc) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 ^ fix2 ^ fix3) + ints[0x5] - 0x5c6be | 0x0) << 0x4 | fix0 >>> 0x1c) + fix1 | 0x0) ^ fix1 ^ fix2) + ints[0x8] - 0x788e097f | 0x0) << 0xb | fix3 >>> 0x15) + fix0 | 0x0) ^ fix0 ^ fix1) + ints[0xb] + 0x6d9d6122 | 0x0) << 0x10 | fix2 >>> 0x10) + fix3 | 0x0) ^ fix3 ^ fix0) + ints[0xe] - 0x21ac7f4 | 0x0) << 0x17 | fix1 >>> 0x9) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 ^ fix2 ^ fix3) + ints[0x1] - 0x5b4115bc | 0x0) << 0x4 | fix0 >>> 0x1c) + fix1 | 0x0) ^ fix1 ^ fix2) + ints[0x4] + 0x4bdecfa9 | 0x0) << 0xb | fix3 >>> 0x15) + fix0 | 0x0) ^ fix0 ^ fix1) + ints[0x7] - 0x944b4a0 | 0x0) << 0x10 | fix2 >>> 0x10) + fix3 | 0x0) ^ fix3 ^ fix0) + ints[0xa] - 0x41404390 | 0x0) << 0x17 | fix1 >>> 0x9) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 ^ fix2 ^ fix3) + ints[0xd] + 0x289b7ec6 | 0x0) << 0x4 | fix0 >>> 0x1c) + fix1 | 0x0) ^ fix1 ^ fix2) + ints[0x0] - 0x155ed806 | 0x0) << 0xb | fix3 >>> 0x15) + fix0 | 0x0) ^ fix0 ^ fix1) + ints[0x3] - 0x2b10cf7b | 0x0) << 0x10 | fix2 >>> 0x10) + fix3 | 0x0) ^ fix3 ^ fix0) + ints[0x6] + 0x4881d05 | 0x0) << 0x17 | fix1 >>> 0x9) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix2 = ((fix2 += ((fix3 = ((fix3 += ((fix0 = ((fix0 += (fix1 ^ fix2 ^ fix3) + ints[0x9] - 0x262b2fc7 | 0x0) << 0x4 | fix0 >>> 0x1c) + fix1 | 0x0) ^ fix1 ^ fix2) + ints[0xc] - 0x1924661b | 0x0) << 0xb | fix3 >>> 0x15) + fix0 | 0x0) ^ fix0 ^ fix1) + ints[0xf] + 0x1fa27cf8 | 0x0) << 0x10 | fix2 >>> 0x10) + fix3 | 0x0) ^ fix3 ^ fix0) + ints[0x2] - 0x3b53a99b | 0x0) << 0x17 | fix1 >>> 0x9) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix3 = ((fix3 += (fix1 ^ ((fix0 = ((fix0 += (fix2 ^ (fix1 | ~fix3)) + ints[0x0] - 0xbd6ddbc | 0x0) << 0x6 | fix0 >>> 0x1a) + fix1 | 0x0) | ~fix2)) + ints[0x7] + 0x432aff97 | 0x0) << 0xa | fix3 >>> 0x16) + fix0 | 0x0) ^ ((fix2 = ((fix2 += (fix0 ^ (fix3 | ~fix1)) + ints[0xe] - 0x546bdc59 | 0x0) << 0xf | fix2 >>> 0x11) + fix3 | 0x0) | ~fix0)) + ints[0x5] - 0x36c5fc7 | 0x0) << 0x15 | fix1 >>> 0xb) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix3 = ((fix3 += (fix1 ^ ((fix0 = ((fix0 += (fix2 ^ (fix1 | ~fix3)) + ints[0xc] + 0x655b59c3 | 0x0) << 0x6 | fix0 >>> 0x1a) + fix1 | 0x0) | ~fix2)) + ints[0x3] - 0x70f3336e | 0x0) << 0xa | fix3 >>> 0x16) + fix0 | 0x0) ^ ((fix2 = ((fix2 += (fix0 ^ (fix3 | ~fix1)) + ints[0xa] - 0x100b83 | 0x0) << 0xf | fix2 >>> 0x11) + fix3 | 0x0) | ~fix0)) + ints[0x1] - 0x7a7ba22f | 0x0) << 0x15 | fix1 >>> 0xb) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix3 = ((fix3 += (fix1 ^ ((fix0 = ((fix0 += (fix2 ^ (fix1 | ~fix3)) + ints[0x8] + 0x6fa87e4f | 0x0) << 0x6 | fix0 >>> 0x1a) + fix1 | 0x0) | ~fix2)) + ints[0xf] - 0x1d31920 | 0x0) << 0xa | fix3 >>> 0x16) + fix0 | 0x0) ^ ((fix2 = ((fix2 += (fix0 ^ (fix3 | ~fix1)) + ints[0x6] - 0x5cfebcec | 0x0) << 0xf | fix2 >>> 0x11) + fix3 | 0x0) | ~fix0)) + ints[0xd] + 0x4e0811a1 | 0x0) << 0x15 | fix1 >>> 0xb) + fix2 | 0x0;
        fix1 = ((fix1 += ((fix3 = ((fix3 += (fix1 ^ ((fix0 = ((fix0 += (fix2 ^ (fix1 | ~fix3)) + ints[0x4] - 0x8ac817e | 0x0) << 0x6 | fix0 >>> 0x1a) + fix1 | 0x0) | ~fix2)) + ints[0xb] - 0x42c50dcb | 0x0) << 0xa | fix3 >>> 0x16) + fix0 | 0x0) ^ ((fix2 = ((fix2 += (fix0 ^ (fix3 | ~fix1)) + ints[0x2] + 0x2ad7d2bb | 0x0) << 0xf | fix2 >>> 0x11) + fix3 | 0x0) | ~fix0)) + ints[0x9] - 0x14792c6f | 0x0) << 0x15 | fix1 >>> 0xb) + fix2 | 0x0;
        fix[0x0] = fix0 + fix[0x0] | 0x0;
        fix[0x1] = fix1 + fix[0x1] | 0x0;
        fix[0x2] = fix2 + fix[0x2] | 0x0;
        fix[0x3] = fix3 + fix[0x3] | 0x0;
    }


    public static int[] toInts(String s) {
        int[] res = new int[16];
        for (int i = 0x0; i < 0x40; i += 0x4)
            res[i >> 0x2] = Character.codePointAt(s, i) + (Character.codePointAt(s, (i + 0x1)) << 0x8) + (Character.codePointAt(s, (i + 0x2)) << 0x10) + (Character.codePointAt(s, (i + 0x3)) << 0x18);
        return res;
    }

    public static String getJoin(int[] array) {
        String[] a = new String[4];
        for (int i = 0; i < array.length; i += 1)
            a[i] = convert(array[i]);
        return String.join("", a);
    }

    public static String convert(int s) {
        String[] dist = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        StringBuilder res = new StringBuilder();
        for (int i = 0x0; i < 0x4; i += 0x1)
            res.append(dist[s >> 0x8 * i + 0x4 & 0xf]).append(dist[s >> 0x8 * i & 0xf]);
        return res.toString();
    }
}
