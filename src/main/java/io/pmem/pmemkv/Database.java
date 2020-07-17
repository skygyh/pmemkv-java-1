/*
 * Copyright 2017-2019, Intel Corporation
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *
 *     * Neither the name of the copyright holder nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.pmem.pmemkv;

import io.pmem.pmemkv.internal.GetKeysBuffersJNICallback;
import io.pmem.pmemkv.internal.GetAllBufferJNICallback;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class Database {

    public Database(String engine, String config) {
        pointer = database_start(engine, config);
    }

    public void stop() {
        if (!stopped) {
            stopped = true;
            database_stop(pointer);
        }
    }

    public boolean stopped() {
        return stopped;
    }

    public void getKeys(GetKeysBuffersCallback callback) {
        database_get_keys_buffer(pointer, (int kb, ByteBuffer k)
                -> callback.process((ByteBuffer) k.rewind().limit(kb)));
    }

    public void getKeys(GetKeysByteArraysCallback callback) {
        database_get_keys_bytes(pointer, callback);
    }

    public void getKeys(GetKeysStringsCallback callback) {
        database_get_keys_string(pointer, callback);
    }

    public void getKeysAbove(ByteBuffer key, GetKeysBuffersCallback callback) {
        database_get_keys_above_buffer(pointer, key.position(), key, (int kb, ByteBuffer k)
                -> callback.process((ByteBuffer) k.rewind().limit(kb)));
    }

    public void getKeysAbove(byte[] key, GetKeysByteArraysCallback callback) {
        database_get_keys_above_bytes(pointer, key, callback);
    }

    public void getKeysAbove(String key, GetKeysStringsCallback callback) {
        database_get_keys_above_string(pointer, key.getBytes(), callback);
    }

    public void getKeysEqualAbove(ByteBuffer key, GetKeysBuffersCallback callback) {
        database_get_keys_equal_above_buffer(pointer, key.position(), key, (int kb, ByteBuffer k)
                -> callback.process((ByteBuffer) k.rewind().limit(kb)));
    }

    public void getKeysEqualAbove(byte[] key, GetKeysByteArraysCallback callback) {
        database_get_keys_equal_above_bytes(pointer, key, callback);
    }

    public void getKeysEqualAbove(String key, GetKeysStringsCallback callback) {
        database_get_keys_equal_above_string(pointer, key.getBytes(), callback);
    }

    public void getKeysEqualBelow(ByteBuffer key, GetKeysBuffersCallback callback) {
        database_get_keys_equal_below_buffer(pointer, key.position(), key, (int kb, ByteBuffer k)
                -> callback.process((ByteBuffer) k.rewind().limit(kb)));
    }

    public void getKeysEqualBelow(byte[] key, GetKeysByteArraysCallback callback) {
        database_get_keys_equal_below_bytes(pointer, key, callback);
    }

    public void getKeysEqualBelow(String key, GetKeysStringsCallback callback) {
        database_get_keys_equal_below_string(pointer, key.getBytes(), callback);
    }

    public void getKeysBelow(ByteBuffer key, GetKeysBuffersCallback callback) {
        database_get_keys_below_buffer(pointer, key.position(), key, (int kb, ByteBuffer k)
                -> callback.process((ByteBuffer) k.rewind().limit(kb)));
    }

    public void getKeysBelow(byte[] key, GetKeysByteArraysCallback callback) {
        database_get_keys_below_bytes(pointer, key, callback);
    }

    public void getKeysBelow(String key, GetKeysStringsCallback callback) {
        database_get_keys_below_string(pointer, key.getBytes(), callback);
    }

    public void getKeysBetween(ByteBuffer key1, ByteBuffer key2, GetKeysBuffersCallback callback) {
        database_get_keys_between_buffer(pointer, key1.position(), key1, key2.position(), key2, (int kb, ByteBuffer k)
                -> callback.process((ByteBuffer) k.rewind().limit(kb)));
    }

    public void getKeysBetween(byte[] key1, byte[] key2, GetKeysByteArraysCallback callback) {
        database_get_keys_between_bytes(pointer, key1, key2, callback);
    }

    public void getKeysBetween(String key1, String key2, GetKeysStringsCallback callback) {
        database_get_keys_between_string(pointer, key1.getBytes(), key2.getBytes(), callback);
    }

    public long countAll() {
        return database_count_all(pointer);
    }

    public long countAbove(ByteBuffer key) {
        return database_count_above_buffer(pointer, key.position(), key);
    }

    public long countAbove(byte[] key) {
        return database_count_above_bytes(pointer, key);
    }

    public long countAbove(String key) {
        return database_count_above_bytes(pointer, key.getBytes());
    }

    public long countEqualAbove(ByteBuffer key) {
        return database_count_equal_above_buffer(pointer, key.position(), key);
    }

    public long countEqualAbove(byte[] key) {
        return database_count_equal_above_bytes(pointer, key);
    }

    public long countEqualAbove(String key) {
        return database_count_equal_above_bytes(pointer, key.getBytes());
    }

    public long countBelow(ByteBuffer key) {
        return database_count_below_buffer(pointer, key.position(), key);
    }

    public long countBelow(byte[] key) {
        return database_count_below_bytes(pointer, key);
    }

    public long countBelow(String key) {
        return database_count_below_bytes(pointer, key.getBytes());
    }

    public long countEqualBelow(ByteBuffer key) {
        return database_count_equal_below_buffer(pointer, key.position(), key);
    }

    public long countEqualBelow(byte[] key) {
        return database_count_equal_below_bytes(pointer, key);
    }

    public long countEqualBelow(String key) {
        return database_count_equal_below_bytes(pointer, key.getBytes());
    }

    public long countBetween(ByteBuffer key1, ByteBuffer key2) {
        boolean isEmpty1 = key1 == null || key1.remaining() == 0;
        boolean isEmpty2 = key2 == null || key2.remaining() == 0;
        if (isEmpty1 && isEmpty2) {
            // should never happen
            return 0L;
        }
        if (isEmpty1) {
            return database_count_below_buffer(pointer, key2.position(), key2);
        } else if (isEmpty2) {
            return database_count_above_buffer(pointer, key1.position(), key1);
        } else {
            return database_count_between_buffer(pointer, key1.position(), key1, key2.position(), key2);
        }
    }

    public long countBetween(byte[] key1, byte[] key2) {
        boolean isEmpty1 = key1 == null || key1.length == 0;
        boolean isEmpty2 = key2 == null || key2.length == 0;
        if (isEmpty1 && isEmpty2) {
            // should never happen
            return 0L;
        }
        if (isEmpty1) {
            return database_count_below_bytes(pointer, key2);
        } else if (isEmpty2) {
            return database_count_above_bytes(pointer, key1);
        } else {
            return database_count_between_bytes(pointer, key1, key2);
        }
    }

    public long countBetween(String key1, String key2) {
        boolean isEmpty1 = key1 == null || key1.length() == 0;
        boolean isEmpty2 = key2 == null || key2.length() == 0;
        if (isEmpty1 && isEmpty2) {
            // should never happen
            return 0L;
        }
        if (isEmpty1) {
            return database_count_below_bytes(pointer, key2.getBytes());
        } else if (isEmpty2) {
            return database_count_above_bytes(pointer, key1.getBytes());
        } else {
            return database_count_between_bytes(pointer, key1.getBytes(), key2.getBytes());
        }
    }

    public void get_all(GetAllBufferCallback callback) {
        database_get_all_buffer(pointer, (int kb, ByteBuffer k, int vb, ByteBuffer v)
                -> callback.process((ByteBuffer) k.rewind().limit(kb), (ByteBuffer) v.rewind().limit(vb)));
    }

    public void get_all(GetAllByteArrayCallback callback) {
        database_get_all_bytes(pointer, callback);
    }

    public void get_all(GetAllStringCallback callback) {
        database_get_all_string(pointer, callback);
    }

    public void get_above(ByteBuffer key, GetAllBufferCallback callback) {
        database_get_above_buffer(pointer, key.position(), key, (int kb, ByteBuffer k, int vb, ByteBuffer v)
                -> callback.process((ByteBuffer) k.rewind().limit(kb), (ByteBuffer) v.rewind().limit(vb)));
    }

    public void get_above(byte[] key, GetAllByteArrayCallback callback) {
        database_get_above_bytes(pointer, key, callback);
    }

    public void get_above(String key, GetAllStringCallback callback) {
        database_get_above_string(pointer, key.getBytes(), callback);
    }

    public void get_equal_above(ByteBuffer key, GetAllBufferCallback callback) {
        database_get_equal_above_buffer(pointer, key.position(), key, (int kb, ByteBuffer k, int vb, ByteBuffer v)
                -> callback.process((ByteBuffer) k.rewind().limit(kb), (ByteBuffer) v.rewind().limit(vb)));
    }

    public void get_equal_above(byte[] key, GetAllByteArrayCallback callback) {
        database_get_equal_above_bytes(pointer, key, callback);
    }

    public void get_equal_above(String key, GetAllStringCallback callback) {
        database_get_equal_above_string(pointer, key.getBytes(), callback);
    }

    public void get_below(ByteBuffer key, GetAllBufferCallback callback) {
        database_get_below_buffer(pointer, key.position(), key, (int kb, ByteBuffer k, int vb, ByteBuffer v)
                -> callback.process((ByteBuffer) k.rewind().limit(kb), (ByteBuffer) v.rewind().limit(vb)));
    }

    public void get_below(byte[] key, GetAllByteArrayCallback callback) {
        database_get_below_bytes(pointer, key, callback);
    }

    public void get_below(String key, GetAllStringCallback callback) {
        database_get_below_string(pointer, key.getBytes(), callback);
    }

    public void get_equal_below(ByteBuffer key, GetAllBufferCallback callback) {
        database_get_equal_below_buffer(pointer, key.position(), key, (int kb, ByteBuffer k, int vb, ByteBuffer v)
                -> callback.process((ByteBuffer) k.rewind().limit(kb), (ByteBuffer) v.rewind().limit(vb)));
    }

    public void get_equal_below(byte[] key, GetAllByteArrayCallback callback) {
        database_get_equal_below_bytes(pointer, key, callback);
    }

    public void get_equal_below(String key, GetAllStringCallback callback) {
        database_get_equal_below_string(pointer, key.getBytes(), callback);
    }

    public void get_between(ByteBuffer key1, ByteBuffer key2, GetAllBufferCallback callback) {
        boolean isEmpty1 = key1 == null || key1.remaining() == 0;
        boolean isEmpty2 = key2 == null || key2.remaining() == 0;
        if (isEmpty1 && isEmpty2) {
            // should never happen
            return;
        }
        if (isEmpty1) {
            get_below(key2, callback);
        } else if (isEmpty2) {
            get_above(key1, callback);
        } else {
            database_get_between_buffer(pointer, key1.position(), key1, key2.position(), key2, (int kb, ByteBuffer k, int vb, ByteBuffer v)
                    -> callback.process((ByteBuffer) k.rewind().limit(kb), (ByteBuffer) v.rewind().limit(vb)));
        }
    }

    public void get_between(byte[] key1, byte[] key2, GetAllByteArrayCallback callback) {
        boolean isEmpty1 = key1 == null || key1.length == 0;
        boolean isEmpty2 = key2 == null || key2.length == 0;
        if (isEmpty1 && isEmpty2) {
            // should never happen
            return;
        }
        if (isEmpty1) {
            get_below(key2, callback);
        } else if (isEmpty2) {
            get_above(key1, callback);
        } else {
            database_get_between_bytes(pointer, key1, key2, callback);
        }
    }

    public void get_between(String key1, String key2, GetAllStringCallback callback) {
        boolean isEmpty1 = key1 == null || key1.length() == 0;
        boolean isEmpty2 = key2 == null || key2.length() == 0;
        if (isEmpty1 && isEmpty2) {
            // should never happen
            return;
        }
        if (isEmpty1) {
            get_below(key2, callback);
        } else if (isEmpty2) {
            get_above(key1, callback);
        } else {
            database_get_between_string(pointer, key1.getBytes(), key2.getBytes(), callback);
        }
    }

    public boolean exists(ByteBuffer key) {
        return database_exists_buffer(pointer, key.position(), key);
    }

    public boolean exists(byte[] key) {
        return database_exists_bytes(pointer, key);
    }

    public boolean exists(String key) {
        return database_exists_bytes(pointer, key.getBytes());
    }

    public void get(ByteBuffer key, ByteBuffer value) {
        int valuebytes = database_get_buffer(pointer, key.position(), key, value.capacity(), value);
        value.rewind();
        value.limit(valuebytes);
    }

    public byte[] get(byte[] key) {
        return database_get_bytes(pointer, key);
    }

    public String get(String key) {
        byte[] result = database_get_bytes(pointer, key.getBytes());
        return result == null ? null : new String(result);
    }

    public void put(ByteBuffer key, ByteBuffer value) {
        try {
            database_put_buffer(pointer, key.position(), key, value.position(), value);
        } catch (DatabaseException kve) {
            kve.setKey(key);
            throw kve;
        }
    }

    public void put(byte[] key, byte[] value) {
        try {
            database_put_bytes(pointer, key, value);
        } catch (DatabaseException kve) {
            kve.setKey(key);
            throw kve;
        }
    }

    public void put(String key, String value) {
        try {
            database_put_bytes(pointer, key.getBytes(), value.getBytes());
        } catch (DatabaseException kve) {
            kve.setKey(key);
            throw kve;
        }
    }

    public boolean remove(ByteBuffer key) {
        try {
            return database_remove_buffer(pointer, key.position(), key);
        } catch (DatabaseException kve) {
            kve.setKey(key);
            throw kve;
        }
    }

    public boolean remove(byte[] key) {
        try {
            return database_remove_bytes(pointer, key);
        } catch (DatabaseException kve) {
            kve.setKey(key);
            throw kve;
        }
    }

    public boolean remove(String key) {
        try {
            return database_remove_bytes(pointer, key.getBytes());
        } catch (DatabaseException kve) {
            kve.setKey(key);
            throw kve;
        }
    }

    public BytesIterator iterator() {
        return new BytesIterator(pointer);
    }

    public class BytesIterator implements Closeable {
        private final long db_pointer;
        private final long iterator_pointer;

        public BytesIterator(final long db_pointer) {
            this.db_pointer = db_pointer;
            this.iterator_pointer = database_iterator_new(db_pointer);
        }

        public void next() {
            database_iterator_next(db_pointer, iterator_pointer);
        }

        public void prev() {
                database_iterator_prev(db_pointer, iterator_pointer);
        }

        public void seekToFirst() {
            database_iterator_seek_to_first(db_pointer, iterator_pointer);
        }

        public void seekToLast() {
            database_iterator_seek_to_last(db_pointer, iterator_pointer);
        }

        public void seek(final byte[] target) {
            database_iterator_seek(db_pointer, iterator_pointer, target);
        }

        public void seekForPrev(final byte[] target) {
            database_iterator_seek_for_prev(db_pointer, iterator_pointer, target);
        }

        public void seekForNext(final byte[] target) {
            database_iterator_seek_for_next(db_pointer, iterator_pointer, target);
        }

        public boolean isValid() {
            return database_iterator_is_valid(db_pointer, iterator_pointer);
        }

        public byte[] key() {
            return database_iterator_key_bytes(db_pointer, iterator_pointer);
        }

        public byte[] value() {
            return database_iterator_value_bytes(db_pointer, iterator_pointer);
        }

        @Override
        public void close() {
            database_iterator_delete(pointer, iterator_pointer);
        }

    }

    public boolean get_floor_entry(byte[] key, GetAllByteArrayCallback callback) {
        return database_get_floor_entry_bytes(pointer, key, callback);
    }

    public boolean get_floor_entry(String key, GetAllStringCallback callback) {
        return database_get_floor_entry_string(pointer, key.getBytes(), callback);
    }

    public boolean get_ceiling_entry(byte[] key, GetAllByteArrayCallback callback) {
        return database_get_ceiling_entry_bytes(pointer, key, callback);
    }

    public boolean get_ceiling_entry(String key, GetAllStringCallback callback) {
        return database_get_ceiling_entry_string(pointer, key.getBytes(), callback);
    }

    public boolean get_lower_entry(byte[] key, GetAllByteArrayCallback callback) {
        return database_get_lower_entry_bytes(pointer, key, callback);
    }

    public boolean get_lower_entry(String key, GetAllStringCallback callback) {
        return database_get_lower_entry_string(pointer, key.getBytes(), callback);
    }

    public boolean get_higher_entry(byte[] key, GetAllByteArrayCallback callback) {
        return database_get_higher_entry_bytes(pointer, key, callback);
    }

    public boolean get_higer_entry(String key, GetAllStringCallback callback) {
        return database_get_higher_entry_string(pointer, key.getBytes(), callback);
    }

    private final long pointer;
    private boolean stopped;

    // JNI METHODS --------------------------------------------------------------------------------

    private native long database_start(String engine, String config);
    private native void database_stop(long ptr);
    private native void database_get_keys_buffer(long ptr, GetKeysBuffersJNICallback cb);
    private native void database_get_keys_bytes(long ptr, GetKeysByteArraysCallback cb);
    private native void database_get_keys_string(long ptr, GetKeysStringsCallback cb);
    private native void database_get_keys_above_buffer(long ptr, int kb, ByteBuffer k, GetKeysBuffersJNICallback cb);
    private native void database_get_keys_above_bytes(long ptr, byte[] k, GetKeysByteArraysCallback cb);
    private native void database_get_keys_above_string(long ptr, byte[] k, GetKeysStringsCallback cb);
    private native void database_get_keys_equal_above_buffer(long ptr, int kb, ByteBuffer k, GetKeysBuffersJNICallback cb);
    private native void database_get_keys_equal_above_bytes(long ptr, byte[] k, GetKeysByteArraysCallback cb);
    private native void database_get_keys_equal_above_string(long ptr, byte[] k, GetKeysStringsCallback cb);
    private native void database_get_keys_equal_below_buffer(long ptr, int kb, ByteBuffer k, GetKeysBuffersJNICallback cb);
    private native void database_get_keys_equal_below_bytes(long ptr, byte[] k, GetKeysByteArraysCallback cb);
    private native void database_get_keys_equal_below_string(long ptr, byte[] k, GetKeysStringsCallback cb);
    private native void database_get_keys_below_buffer(long ptr, int kb, ByteBuffer k, GetKeysBuffersJNICallback cb);
    private native void database_get_keys_below_bytes(long ptr, byte[] k, GetKeysByteArraysCallback cb);
    private native void database_get_keys_below_string(long ptr, byte[] k, GetKeysStringsCallback cb);
    private native void database_get_keys_between_buffer(long ptr, int kb1, ByteBuffer k1, int kb2, ByteBuffer k2, GetKeysBuffersJNICallback cb);
    private native void database_get_keys_between_bytes(long ptr, byte[] k1, byte[] k2, GetKeysByteArraysCallback cb);
    private native void database_get_keys_between_string(long ptr, byte[] k1, byte[] k2, GetKeysStringsCallback cb);
    private native long database_count_all(long ptr);
    private native long database_count_above_buffer(long ptr, int kb, ByteBuffer k);
    private native long database_count_above_bytes(long ptr, byte[] k);
    private native long database_count_equal_above_buffer(long ptr, int kb, ByteBuffer k);
    private native long database_count_equal_above_bytes(long ptr, byte[] k);
    private native long database_count_equal_below_buffer(long ptr, int kb, ByteBuffer k);
    private native long database_count_equal_below_bytes(long ptr, byte[] k);
    private native long database_count_below_buffer(long ptr, int kb, ByteBuffer k);
    private native long database_count_below_bytes(long ptr, byte[] k);
    private native long database_count_between_buffer(long ptr, int kb1, ByteBuffer k1, int kb2, ByteBuffer k2);
    private native long database_count_between_bytes(long ptr, byte[] k1, byte[] k2);
    private native void database_get_all_buffer(long ptr, GetAllBufferJNICallback cb);
    private native void database_get_all_bytes(long ptr, GetAllByteArrayCallback cb);
    private native void database_get_all_string(long ptr, GetAllStringCallback cb);
    private native void database_get_above_buffer(long ptr, int kb, ByteBuffer k, GetAllBufferJNICallback cb);
    private native void database_get_above_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native void database_get_above_string(long ptr, byte[] k, GetAllStringCallback cb);
    private native void database_get_equal_above_buffer(long ptr, int kb, ByteBuffer k, GetAllBufferJNICallback cb);
    private native void database_get_equal_above_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native void database_get_equal_above_string(long ptr, byte[] k, GetAllStringCallback cb);
    private native void database_get_below_buffer(long ptr, int kb, ByteBuffer k, GetAllBufferJNICallback cb);
    private native void database_get_below_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native void database_get_below_string(long ptr, byte[] k, GetAllStringCallback cb);
    private native void database_get_equal_below_buffer(long ptr, int kb, ByteBuffer k, GetAllBufferJNICallback cb);
    private native void database_get_equal_below_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native void database_get_equal_below_string(long ptr, byte[] k, GetAllStringCallback cb);
    private native void database_get_between_buffer(long ptr, int kb1, ByteBuffer k1, int kb2, ByteBuffer k2, GetAllBufferJNICallback cb);
    private native void database_get_between_bytes(long ptr, byte[] k1, byte[] k2, GetAllByteArrayCallback cb);
    private native void database_get_between_string(long ptr, byte[] k1, byte[] k2, GetAllStringCallback cb);
    private native boolean database_exists_buffer(long ptr, int kb, ByteBuffer k);
    private native boolean database_exists_bytes(long ptr, byte[] k);
    private native int database_get_buffer(long ptr, int kb, ByteBuffer k, int vb, ByteBuffer v);
    private native byte[] database_get_bytes(long ptr, byte[] k);
    private native void database_put_buffer(long ptr, int kb, ByteBuffer k, int vb, ByteBuffer v);
    private native void database_put_bytes(long ptr, byte[] k, byte[] v);
    private native boolean database_remove_buffer(long ptr, int kb, ByteBuffer k);
    private native boolean database_remove_bytes(long ptr, byte[] k);
    private native long database_iterator_new(long ptr);
    private native void database_iterator_next(long ptr, long it_ptr);
    private native void database_iterator_prev(long ptr, long it_ptr);
    private native void database_iterator_seek_to_first(long ptr, long it_ptr);
    private native void database_iterator_seek_to_last(long ptr, long it_ptr);
    private native void database_iterator_seek(long ptr, long it_ptr, byte[] k);
    private native void database_iterator_seek_for_prev(long ptr, long it_ptr, byte[] k);
    private native void database_iterator_seek_for_next(long ptr, long it_ptr, byte[] k);
    private native boolean database_iterator_is_valid(long ptr, long it_ptr);
    private native byte[] database_iterator_key_bytes(long ptr, long it_ptr);
    private native byte[] database_iterator_value_bytes(long ptr, long it_ptr);
    private native void database_iterator_delete(long ptr, long it_ptr);

    private native boolean database_get_floor_entry_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native boolean database_get_floor_entry_string(long ptr, byte[] k, GetAllStringCallback cb);

    private native boolean database_get_ceiling_entry_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native boolean database_get_ceiling_entry_string(long ptr, byte[] k, GetAllStringCallback cb);


    private native boolean database_get_lower_entry_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native boolean database_get_lower_entry_string(long ptr, byte[] k, GetAllStringCallback cb);

    private native boolean database_get_higher_entry_bytes(long ptr, byte[] k, GetAllByteArrayCallback cb);
    private native boolean database_get_higher_entry_string(long ptr, byte[] k, GetAllStringCallback cb);


    static {
        System.loadLibrary("pmemkv-jni");
    }

}
