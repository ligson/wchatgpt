package org.ligson.ichat.photo.zuotang;

import java.util.Arrays;

public class AuthorizationPart {

    private static int a = 8;
    private static String i = "=";

    private String tt(String e, String t) {
        return b(e, t);
    }

    private int o(int r, int m, int c, int l) {
        return r < 20 ? m & c | ~m & l : r < 40 ? m ^ c ^ l : r < 60 ? m & c | m & l | c & l : m ^ c ^ l;
    }

    private int s(int r) {
        return r < 20 ? 1518500249 : r < 40 ? 1859775393 : r < 60 ? -1894007588 : -899497514;
    }

    private int n(int r, int m) {
        int c = (r & 65535) + (m & 65535), l = (r >> 16) + (m >> 16) + (c >> 16);
        return l << 16 | c & 65535;
    }

    private int p(int r, int m) {
        return r << m | r >>> 32 - m;
    }

    private int[] u(int[] r, int m) {
        r = resize(r, m >> 5);
        r[m >> 5] |= 128 << 24 - m % 32;
        r = resize(r, (m + 64 >> 9 << 4) + 15);
        r[(m + 64 >> 9 << 4) + 15] = m;
        int[] c = new int[80];
        int l = 1732584193, g = -271733879, f = -1732584194, y = 271733878, w = -1009589776, T = 0;
        for (; T < r.length; T += 16) {
            int I = l, L = g, M = f, K = y, J = w, h = 0;
            for (; h < 80; h++) {
                if (h < 16) {
                    c[h] = r[T + h];
                } else {
                    c[h] = p(c[h - 3] ^ c[h - 8] ^ c[h - 14] ^ c[h - 16], 1);
                }
                int N = n(n(p(l, 5), o(h, g, f, y)), n(n(w, c[h]), s(h)));
                w = y;
                y = f;
                f = p(g, 30);
                g = l;
                l = N;
            }
            l = n(l, I);
            g = n(g, L);
            f = n(f, M);
            y = n(y, K);
            w = n(w, J);
        }
        int[] kk = new int[5];
        kk[0] = l;
        kk[1] = g;
        kk[2] = f;
        kk[3] = y;
        kk[4] = w;
        return kk;
    }

    private int[] resize(int[] r, int size) {
        if (size >= r.length) {
            r = Arrays.copyOf(r, size + 1);
        }
        return r;
    }

    private int[] d(String r) {
        int[] m = new int[0];
        for (int c = (1 << a) - 1, l = 0; l < r.length() * a; l += a) {
            m = resize(m, l >> 5);
            m[l >> 5] |= (Character.codePointAt(r, (l / 8)) & c) << 32 - a - l % 32;
        }
        return m;
    }

    private int[] x(String r, String m) {
        int[] c = d(r);
        if (c.length > 16) {
            c = u(c, r.length() * a);
        }
        int[] l = new int[16];
        int[] g = new int[16];
        for (int f = 0; f < 16; f++) {
            c = resize(c, f);
            l[f] = c[f] ^ 909522486;
            g[f] = c[f] ^ 1549556828;
        }
        int[] y = u(concat(l, d(m)), 512 + m.length() * a);
        return u(concat(g, y), 512 + 160);
    }

    private String v(int[] r) {
        String m = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", c = "";
        for (int l = 0; l < r.length * 4; l += 3) {
            r = resize(r, l + 2 >> 2);
            int g = (r[l >> 2] >> 8 * (3 - l % 4) & 255) << 16 | (r[l + 1 >> 2] >> 8 * (3 - (l + 1) % 4) & 255) << 8 | r[l + 2 >> 2] >> 8 * (3 - (l + 2) % 4) & 255;

            for (int f = 0; f < 4; f++) {
                if (l * 8 + f * 6 > r.length * 32) {
                    c += i;
                } else {
                    c += m.charAt(g >> 6 * (3 - f) & 63);
                }
            }
        }
        return c;
    }

    private String b(String r, String m) {
        return v(x(r, m));
    }

    private int[] concat(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        int j = 0;
        for (int x = 0; x < a.length; x++) {
            c[j] = a[x];
            j++;
        }
        for (int x = 0; x < b.length; x++) {
            c[j] = b[x];
            j++;
        }
        return c;
    }

    public static String getPart(String accessKeySecret,String bucket,String objectName,String tokenSecret,String date,String callback) {
        String png = objectName.substring(objectName.lastIndexOf(".") + 1);
        String str = "PUT\n\n" +
                "image/" + png + "\n" +
                date + "\n" +
                "x-oss-callback:" + callback + "\n" +
                "x-oss-date:" + date + "\n" +
                "x-oss-security-token:" + tokenSecret + "\n" +
                "/" + bucket + "/" + objectName + "";
        String tt = new AuthorizationPart().tt(accessKeySecret, str);
        return (tt.replace("AAAAA",i));
    }
}
//471HZsQb0oDmvmj82RQNs63jT4IAAAAA
//471HZsQb0oDmvmj82RQNs63jT4IAAAAA
//471HZsQb0oDmvmj82RQNs63jT4IAAAAA