package me.thespeace.restapiwithspring.configs;

import me.thespeace.restapiwithspring.accounts.Account;
import me.thespeace.restapiwithspring.accounts.AccountRole;
import me.thespeace.restapiwithspring.accounts.AccountService;
import me.thespeace.restapiwithspring.common.BaseControllerTest;
import me.thespeace.restapiwithspring.common.TestDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    /**
     * <p>인증 토큰을 받기 위해 Grant Type의 인증 방법 6가지 중 2가지만 사용.</p>
     * <p>Password, Refresh Token만 지원, 최초의 OAuth 토큰은 Password로 발급.</p>
     * <p>Password의 특징으로는 요청과 응답이 한번에 이루어진다.(홉이 하나)</p>
     * <p>Refresh Token은 새로운 엑세스 토큰을 발급받는 용도.</p>
     */
    @Test
    @DisplayName("인증 토큰을 발급 받는 테스트")
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception{
        // Given
        String username = "thespeace1@gmail.com";
        String password = "pass";
        Account thespeace = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(thespeace);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username",username)
                .param("password",password)
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}