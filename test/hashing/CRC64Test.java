package hashing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CRC64Test
{
    @Test
    public void shouldCrc64() {

        shouldCrc64("123456789".getBytes(), -7395533204333446662L);
        shouldCrc64("This is a test of the emergency broadcast system.".getBytes(), 2871916124362751090L);
        shouldCrc64("IHATEMATH".getBytes(), 4116537408385638600L);
    }

    private void shouldCrc64(byte[] bytes, long expectedValue) {
        CRC64 crc64 = new CRC64(bytes, bytes.length);
        System.out.println(String.format("0x%08X", crc64.getValue()));
        assertThat(crc64.getValue()).isEqualTo(expectedValue);
    }
}
