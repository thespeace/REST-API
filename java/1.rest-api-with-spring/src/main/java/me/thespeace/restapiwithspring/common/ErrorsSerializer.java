package me.thespeace.restapiwithspring.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

/**
 * <h1>커스텀 JSON Serializer</h1>
 * <p>스프링이 제공하는 errors를 json으로 변환할 Serializer</p>
 * <ul>
 *     <li>extends JsonSerializer<T>(Jackson JSON 제공)</li>
 *     <li>@JsonComponent(스프링 부트 제공)</li>
 * </ul>
 * <p/>
 *
 * <h2>왜 만들어야 하나?</h2>
 * <p>우리의 컨트롤러에 produces를 HAL 스펙을 따르는 JSON으로 변환해서 응답을 주겠다고
 * 명시했기 때문에 이를 따라야한다.</p>
 * <p>errors 객체를 JSON으로 자동 변환을 할 수가 없기 때문에 에러가 발생.
 * 그렇다면 ...body(event); 는 자동 변환되고, ...body(errors); 는 왜 안될까?</p>
 * <p>event는 자바 빈 스펙을 따르기 떄문에 이러한 도메인은 BeanSerializer를 사용해서
 * 자바 빈 스펙을 준수한 객체를 Serialization(객체 -> json, 반대는 Deserialization(역직렬화))해준다.</p>
 * <p>Serializer도 여러가지가 등록되어 있는데 우리는 바디에 담아준 이 이벤트 객체를 JSON으로 변환할때,
 * ObjectMapper를 써서 변환을 하는데 그때 ObjectMapper는 Bean Serializer를 사용해서 자바 빈 스펙을
 * 준수한 객체이기 때문에 이 객체의 정보를 JSON으로 변환할 수 있었던 것이다. 아무런 커스텀 시리얼라이저가 없이도
 * 기본으로 등록되어 있는 빈 시리얼라이저를 사용해서 JSON으로 변환할 수 있었던 것이다.</p><br>
 * <p>하지만 Errors 같은 경우에는 자바 빈 스펙을 준수하고 있는 객체가 아니다. 그렇기 때문에 기본 빈 시리얼라이저를
 * 사용해서 JSON으로 변환할 수가 없다. 따라서 커스텀으로 직접 작성을 해주어야 한다.</p>
 *
 *
 * <p/>
 *
 * <h2>BindingError</h2>
 * <ul>
 *     <li>FieldError 와 GlobalError(ObjectError)가 있다.</li>
 *     <li>objectName</li>
 *     <li>defaultMessage</li>
 *     <li>code</li>
 *     <li>field</li>
 *     <li>rejectedValue</li>
 * </ul>
 */
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> { // 제네릭으로 어떤 타입을 위한 Serializer인지 명시-


    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeFieldName("errors");
        gen.writeStartArray();

        // filed errors
        errors.getFieldErrors().forEach(e -> {
            try { // filed errors
                gen.writeStartObject();
                gen.writeStringField("field", e.getField());
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                Object rejectedValue = e.getRejectedValue();
                if (rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());
                }
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        // global Error
        errors.getGlobalErrors().forEach(e -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        gen.writeEndArray();
    }
}
