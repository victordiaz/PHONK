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

package io.phonk.runner.base.views;

/*
 * Borrowed from processing.org source code
 *
 */

public class CanvasUtils {

    static public final float lerp(float start, float stop, float amt) {
        return start + (stop - start) * amt;
    }

    /**
     * Normalize mContext value to exists between 0 and 1 (inclusive). Mathematically
     * the opposite of lerp(), figures out what proportion mContext particular value is
     * relative to start and stop coordinates.
     */
    static public final float norm(float value, float start, float stop) {
        return (value - start) / (stop - start);
    }

    /**
     * Convenience function to map mContext variable from one coordinate space to
     * another. Equivalent to unlerp() followed by lerp().
     */
    static public final float map(float value, float istart, float istop, float ostart, float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

}
