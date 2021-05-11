package com.se.term;

import java.nio.ByteBuffer;

import static com.se.term.ByteModule.btoi;

public class RandomNumberGenerator {

    /*
     * Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura, All rights
     * reserved. Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     * 1. Redistributions of source code must retain the above copyright notice,
     * this list of conditions and the following disclaimer. 2. Redistributions in
     * binary form must reproduce the above copyright notice, this list of
     * conditions and the following disclaimer in the documentation and/or other
     * materials provided with the distribution. 3. The names of its contributors
     * may not be used to endorse or promote products derived from this software
     * without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
     * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
     * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
     * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
     * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
     * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
     * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
     * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
     * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
     * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

    final int N = 624; // MT19937_32 initialization
    final int M = 397;
    final long MATRIX_A = 0x9908b0dfL;
    final long UMASK = 0x80000000L;
    final long LMASK = 0x7fffffffL;

    public byte[] MT19937(byte[] seed) { // 메르센 트위스터

        long mt[] = new long[N];
        int mti = N + 1;

        int s = btoi(seed);
        mt[0] = s & 0xFFFFFFFF;
        for (mti = 1; mti < N; mti++) {
            mt[mti] = (0x6C078965 * (mt[mti - 1] ^ (mt[mti - 1] >> 30)) + mti);
        }

        long y;
        long mag01[] = new long[2];
        mag01[0] = 0x0L;
        mag01[1] = MATRIX_A;

        if (mti >= N) {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UMASK) | (mt[kk + 1] & LMASK);
                mt[kk] = mt[kk + M] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            }

            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UMASK) | (mt[kk + 1] & LMASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            }

            y = (mt[N - 1] & UMASK) | (mt[0] & LMASK);
            mt[N - 1] = mt[M - 1] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];

            mti = 0;
        }

        y = mt[mti++];

        y ^= (y >> 11);
        y ^= (y >> 7) & 0x9D2C6780L;
        y ^= (y >> 15) & 0xEFC60000L;
        y ^= (y >> 18);

        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(y);
        byte[] result = buf.array();

        return (byte[]) result;
    }

    public long MT19937_long(int seed) { // 메르센 트위스터 long 반환용

        long mt[] = new long[N];
        int mti = N + 1;

        int s = seed;
        mt[0] = s & 0xFFFFFFFF;
        for (mti = 1; mti < N; mti++) {
            mt[mti] = (0x6C078965 * (mt[mti - 1] ^ (mt[mti - 1] >> 30)) + mti);
        }

        long y;
        long mag01[] = new long[2];
        mag01[0] = 0x0L;
        mag01[1] = MATRIX_A;

        if (mti >= N) {
            int kk;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UMASK) | (mt[kk + 1] & LMASK);
                mt[kk] = mt[kk + M] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            }

            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UMASK) | (mt[kk + 1] & LMASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];
            }

            y = (mt[N - 1] & UMASK) | (mt[0] & LMASK);
            mt[N - 1] = mt[M - 1] ^ (y >> 1) ^ mag01[(int) (y & 0x1L)];

            mti = 0;
        }

        y = mt[mti++];

        y ^= (y >> 11);
        y ^= (y >> 7) & 0x9D2C6780L;
        y ^= (y >> 15) & 0xEFC60000L;
        y ^= (y >> 18);

        return y;
    }
}
