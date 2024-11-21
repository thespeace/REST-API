package me.thespeace.restapiwithspring.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>스프링 시큐리티 현재 사용자 커스텀 애노테이션</h1>
 * <ul>
 *     <li>@AuthenticationPrincipal = {@code Authentication authentication = SecurityContextHolder.getContext().getAuthentication();} :
 *         자바 ThreadLocal 기반 구현으로 인증 정보를 담고 있다.</li>
 *     <li>@AuthenticationPrincipal spring.security.User user : 인증 안한 경우에 null, 인증 한 경우에는 username과 authorities 참조 가능</li>
 *     <li></li>
 * </ul>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") //SpEL: Spring Expression Language의 유연함!
public @interface CurrentUser {
}
