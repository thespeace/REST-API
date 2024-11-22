package me.thespeace.restapiwithspring.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * <h1>출력 값 제한</h1>
 * <p>응답에서 owner의 id만 보내 주기 위해 @JsonSerializer 설정</p>
 */
public class AccountSerializer extends JsonSerializer<Account> {
    @Override
    public void serialize(Account account, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id",account.getId());
        gen.writeEndObject();
    }
}
