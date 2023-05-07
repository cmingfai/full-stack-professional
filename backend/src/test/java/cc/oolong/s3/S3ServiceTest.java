package cc.oolong.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    private S3Service underTest;

    @Mock
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        underTest=new S3Service(s3Client);
    }

    @Test
    void canPutObject() throws IOException {
        // given
        String bucket="customer";
        String key="foo";
        byte[] data="Hello World".getBytes();

        PutObjectRequest expectedPutObjectRequest=
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
        RequestBody expectedRequestBody=RequestBody.fromBytes(data);


        // when
        underTest.putObject(bucket,key,data);
        
        // then
        ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor
                =ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> requestBodyArgumentCaptor
                =ArgumentCaptor.forClass(RequestBody.class);

       verify(s3Client).putObject(
               putObjectRequestArgumentCaptor.capture(),
               requestBodyArgumentCaptor.capture());

        PutObjectRequest capturedPutObjectRequest = putObjectRequestArgumentCaptor.getValue();
        RequestBody capturedRequestBody = requestBodyArgumentCaptor.getValue();

        assertThat(capturedPutObjectRequest).isEqualTo(expectedPutObjectRequest);
        assertThat(capturedPutObjectRequest.bucket()).isEqualTo(expectedPutObjectRequest.bucket());
        assertThat(capturedPutObjectRequest.key()).isEqualTo(expectedPutObjectRequest.key());

        assertThat(capturedRequestBody.contentStreamProvider().newStream().readAllBytes())
                .isEqualTo(expectedRequestBody.contentStreamProvider().newStream().readAllBytes());
    }

    @Test
    void canGetObject() throws IOException {
        // given
        String bucket="customer";
        String key="foo";
        byte[] data="Hello World".getBytes();

        GetObjectRequest expectedGetObjectRequest=
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

        ResponseInputStream<GetObjectResponse> res =mock(ResponseInputStream.class);

        when(s3Client.getObject(eq(expectedGetObjectRequest))).thenReturn(res);
        when(res.readAllBytes()).thenReturn(data);
        // when
        byte[] bytes = underTest.getObject(bucket, key);

        // then
        assertThat(bytes).isEqualTo(data);
    }

    @Test
    void willThrowWhenGetObject() throws IOException {
        // given
        String bucket="customer";
        String key="foo";
        byte[] data="Hello World".getBytes();

        GetObjectRequest expectedGetObjectRequest=
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

        ResponseInputStream<GetObjectResponse> res =mock(ResponseInputStream.class);
        when(s3Client.getObject(eq(expectedGetObjectRequest))).thenReturn(res);

        when(res.readAllBytes()).thenThrow(new IOException("Cannot read bytes"));

        // when
        // then
        assertThatThrownBy(
                ()->underTest.getObject(bucket, key)
        ).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot read bytes")
                .hasCauseInstanceOf(IOException.class);


    }
}