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

package io.phonk.runner.base.hardware;

/*
 * AdkPort RingBuffer library
 * 
 * Written by Giles Barton-Owen
 * 
 * This library simply is a single byte ring buffer put in a class, so the library doesn't have to worry so much about the buffer.
 * 
 * 
 */

public class RingBuffer {

    private int length = 16348;
    private byte[] buffer;
    private int start;
    private int end;
    private int count;


    public RingBuffer(int buflen) {
        length = buflen;
        buffer = new byte[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = 0;
        }
        end = 0;
        start = 0;
        count = 0;
    }

    public int available() {
        return count;
    }

    public byte[] getAll() {
        byte[] temp;
        if (count > 0) {
            temp = new byte[count];

            for (int i = 0; i < count; i++) {
                temp[i] = buffer[start];
                start++;
                start = wrap(start);
            }
            count = 0;
        } else {
            temp = new byte[1];
            temp[0] = 0;

        }
        return temp;
    }

    public byte[] get(int len) {
        byte[] temp = new byte[len];
        for (int i = 0; i < len; i++) {
            temp[i] = buffer[wrap(start)];
            start++;
            start = wrap(start);

        }
        count -= len;
        return temp;
    }

    private int wrap(int i) {
        // TODO Auto-generated method stub
        while (i >= length) {
            i -= length;
        }
        return i;
    }

    public void add(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            buffer[end] = b[i];
            end++;
            end = wrap(end);
            count++;
        }
    }

    public void add(byte[] b, int offset, int len) {
        for (int i = 0; i < len; i++) {
            buffer[end] = b[i + offset];
            end++;
            end = wrap(end);
            count++;
        }
    }

    public int getC() {
        start++;
        count--;
        return buffer[start - 1];
    }


}