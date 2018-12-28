package org.jaun.clubmanager.identity.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceBeanTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserApplicationServiceBean userApplicationServiceBean;

    @Test
    public void getUser() throws Exception {
        // given
        User user = new User(new UserId("abc"), "name", asList(Role.BOARD_MEMBER));
        when(userRepository.getUser(any(UserId.class))).thenReturn(user);

        // when
        User returnedUser = userApplicationServiceBean.getUser(new UserId("abc"));

        // then
        assertThat(returnedUser, equalTo(user));
        verify(userRepository).getUser(eq(user.getId()));
    }
}