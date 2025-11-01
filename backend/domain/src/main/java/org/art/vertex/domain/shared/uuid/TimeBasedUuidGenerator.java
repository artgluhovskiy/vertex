package org.art.vertex.domain.shared.uuid;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

public class TimeBasedUuidGenerator implements UuidGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public UUID generate() {
        return generateTimeOrderedUuid();
    }

    private UUID generateTimeOrderedUuid() {
        long timestamp = Instant.now().toEpochMilli();

        byte[] randomBytes = new byte[10];
        RANDOM.nextBytes(randomBytes);

        ByteBuffer buffer = ByteBuffer.allocate(16);

        buffer.putLong(timestamp << 16 | (randomBytes[0] & 0xFF) << 8 | (randomBytes[1] & 0xFF));

        buffer.put(randomBytes, 2, 8);

        buffer.position(6);
        byte versionByte = buffer.get();
        buffer.position(6);
        buffer.put((byte) ((versionByte & 0x0F) | 0x70));

        buffer.position(8);
        byte variantByte = buffer.get();
        buffer.position(8);
        buffer.put((byte) ((variantByte & 0x3F) | 0x80));

        buffer.rewind();
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();

        return new UUID(mostSigBits, leastSigBits);
    }
}
