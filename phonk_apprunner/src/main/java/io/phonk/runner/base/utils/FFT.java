/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.base.utils;


public class FFT {
    int n, m;

    // Lookup tables. Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;

    public FFT(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is mContext power of 2
        if (n != (1 << m)) {
            throw new RuntimeException("FFT length must be power of 2");
        }

        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }
    }

    /**
     * ************************************************************
     * fft.c Douglas L. Jones University of Illinois at Urbana-Champaign January
     * 19, 1992 http://cnx.rice.edu/content/m12016/latest/
     * <p/>
     * fft: in-place radix-2 DIT DFT of mContext complex input
     * <p/>
     * input: n: length of FFT: must be mContext power of two m: n = 2**m input/output
     * x: double array of length n with real part of data y: double array of
     * length n with imag part of data
     * <p/>
     * Permission to copy and use this program is granted as long as this header
     * is included.
     * **************************************************************
     */
    public void fft(double[] re, double[] im) {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;

        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = re[i];
                re[i] = re[j];
                re[j] = t1;
                t1 = im[i];
                im[i] = im[j];
                im[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2) {
                    t1 = c * re[k + n1] - s * im[k + n1];
                    t2 = s * re[k + n1] + c * im[k + n1];
                    re[k + n1] = re[k] - t1;
                    im[k + n1] = im[k] - t2;
                    re[k] = re[k] + t1;
                    im[k] = im[k] + t2;
                }
            }
        }
    }

}