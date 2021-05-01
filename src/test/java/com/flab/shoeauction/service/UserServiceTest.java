package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.AddressDto;
import com.flab.shoeauction.controller.dto.UserDto;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.addressBook.AddressRepository;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.HasProgressingTradeException;
import com.flab.shoeauction.exception.user.HasRemainingPointException;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @ExtendWith : Junit5의 확장 어노테이션을 사용할 수 있다.
 * @Mock : mock 객체를 생성한다.
 * @InjectMock : @Mock이 붙은 객체를 @InjectMock이 붙은 객체에 주입시킬 수 있다.
 */

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    EncryptionService encryptionService;
    @Mock
    AddressRepository addressRepository;
    @Mock
    TradeService tradeService;

    @InjectMocks
    UserService userService;

    private UserDto.SaveRequest createUserDto() {
        UserDto.SaveRequest saveRequest = UserDto.SaveRequest.builder()
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();
        return saveRequest;
    }

    private User createUser() {
        return createUserDto().toEntity();
    }


    @Test
    @DisplayName("이메일 중복으로 회원가입에 실패한다.")
    public void emailDuplicate() {
        UserDto.SaveRequest saveRequest = createUserDto();
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.save(saveRequest));

        verify(userRepository, atLeastOnce()).existsByEmail("test123@test.com");
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입에 실패한다.")
    public void nicknameDuplicate() {
        UserDto.SaveRequest saveRequest = createUserDto();

        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () -> userService.save(saveRequest));

        verify(userRepository, atLeastOnce()).existsByNickname("17171771");
    }

    @Test
    @DisplayName("비밀번호 찾기 성공 - 전달받은 객체(이메일)가 회원이라면 비밀번호 변경에 성공한다.")
    public void updatePasswordByForget() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
            .email("test123@test.com")
            .passwordAfter("test12345")
            .build();
        User user = createUserDto().toEntity();

        when(userRepository.findByEmail(changePasswordRequest.getEmail()))
            .thenReturn(Optional.of(user));

        userService.updatePasswordByForget(changePasswordRequest);

        assertThat(user.getPassword()).isEqualTo(changePasswordRequest.getPasswordAfter());

        verify(userRepository, atLeastOnce()).findByEmail(changePasswordRequest.getEmail());
    }

    @Test
    @DisplayName("가입된 이메일 입력시 비밀번호 찾기(재설정)에 필요한 리소스를 리턴한다.")
    public void SuccessToGetUserResource() {
        String email = "test123@test.com";
        User user = createUserDto().toEntity();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        FindUserResponse userResource = userService.getUserResource(email);

        assertThat(userResource.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResource.getPhone()).isEqualTo(user.getPhone());

    }

    @Test
    @DisplayName("존재하지 않는 이메일 입력시 비밀번호 찾기(재설정)에 필요한 리소스 리턴에 실패한다.")
    public void failToGetUserResource() {

        String email = "non@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userService.getUserResource("non@test.com"));

        verify(userRepository, atLeastOnce()).findByEmail(email);
    }

    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호와 일치하면 비밀번호 변경에 성공한다.")
    public void updatePassword() {
        User user = createUserDto().toEntity();
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
            .builder()
            .email(null)
            .passwordBefore("test1234")
            .passwordAfter("test12345")
            .build();

        String email = "test123@test.com";
        String passwordBefore = encryptionService
            .encrypt(changePasswordRequest.getPasswordBefore());
        String passwordAfter = encryptionService.encrypt(changePasswordRequest.getPasswordAfter());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(email, passwordBefore)).thenReturn(true);

        userService.updatePassword(email, changePasswordRequest);

        assertThat(user.getPassword()).isEqualTo(passwordAfter);

        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByEmailAndPassword(email, passwordBefore);
    }

    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하지 않으면 비밀번호 변경에 실패한다.")
    public void failToUpdatePassword() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
            .builder()
            .email(null)
            .passwordBefore("test1234")
            .passwordAfter("test12345")
            .build();
        String email = "test123@test.com";
        String passwordBefore = encryptionService
            .encrypt(changePasswordRequest.getPasswordBefore());

        when(userRepository.existsByEmailAndPassword(email, passwordBefore)).thenReturn(false);

        assertThrows(UnauthenticatedUserException.class,
            () -> userService.updatePassword(email, changePasswordRequest));

        verify(userRepository, atLeastOnce()).existsByEmailAndPassword(email, passwordBefore);
    }

    @Test
    @DisplayName("계좌 설정 - 환급받을 계좌번호 설정(수정)에 성공한다.")
    public void updateAccount() {
        Account account = new Account("카카오뱅크", "123456789", "루루삐");
        User user = createUserDto().toEntity();
        String email = "test123@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.updateAccount(email, account);

        assertThat(user.getAccount().getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(user.getAccount().getBankName()).isEqualTo(account.getBankName());
        assertThat(user.getAccount().getDepositor()).isEqualTo(account.getDepositor());
        verify(userRepository, atLeastOnce()).findByEmail(email);
    }

    @Test
    @DisplayName("주소록 수정 -주소록에 등록된 주소들 중 하나를 선택하여 수정한다.")
    public void updateAddressBook() {
        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        AddressDto.SaveRequest requestDto = AddressDto.SaveRequest.builder()
            .id(1L)
            .addressName("새 집")
            .roadNameAddress("새집로 123")
            .detailedAddress("789동 123호")
            .postalCode("23456")
            .build();

        when(addressRepository.findById(requestDto.getId())).thenReturn(Optional.of(address));
        userService.updateAddress(requestDto);

        assertThat(address.getAddressName()).isEqualTo(requestDto.getAddressName());
        assertThat(address.getDetailedAddress()).isEqualTo(requestDto.getDetailedAddress());
        assertThat(address.getPostalCode()).isEqualTo(requestDto.getPostalCode());
        assertThat(address.getRoadNameAddress()).isEqualTo(requestDto.getRoadNameAddress());
        verify(addressRepository, atLeastOnce()).findById(any());
    }

    @Test
    @DisplayName("주소록 추가 - 올바른 주소 입력시 주소록 추가에 성공한다.")
    public void addAddressBook() {

        AddressBook addressBook = new AddressBook();

        User user = User.builder()
            .email("test123@test.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01020180103")
            .addressBook(addressBook)
            .build();

        AddressDto.SaveRequest requestDto = AddressDto.SaveRequest.builder()
            .id(1L)
            .addressName("새 집")
            .roadNameAddress("새집로 123")
            .detailedAddress("789동 123호")
            .postalCode("23456")
            .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        userService.addAddress(user.getEmail(), requestDto);

        assertThat(user.getAddressBook().getAddressList().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("주소록 조회 - 해당 USER의 주소록을 불러온다.")
    public void getAddressBooks() {
        AddressBook addressBook = new AddressBook();

        AddressDto.SaveRequest requestDto = AddressDto.SaveRequest.builder()
            .id(1L)
            .addressName("새 집")
            .roadNameAddress("새집로 123")
            .detailedAddress("789동 123호")
            .postalCode("23456")
            .build();

        addressBook.addAddress(requestDto.toEntity());

        User user = User.builder()
            .email("test123@test.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01020180103")
            .addressBook(addressBook)
            .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        List<Address> addressList = userService.getAddressBook(user.getEmail());

        assertThat(addressList.size()).isEqualTo(1);
        assertThat(addressList.get(0).getAddressName()).isEqualTo(requestDto.getAddressName());
        assertThat(addressList.get(0).getDetailedAddress())
            .isEqualTo(requestDto.getDetailedAddress());
        assertThat(addressList.get(0).getPostalCode()).isEqualTo(requestDto.getPostalCode());
        assertThat(addressList.get(0).getRoadNameAddress())
            .isEqualTo(requestDto.getRoadNameAddress());
        verify(userRepository, atLeastOnce()).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("닉네임 변경 성공 - 중복되지 않은 닉네임을 사용하며 닉네임을 변경한지 7일이 초과되었을 경우 닉네임 변경에 성공한다.")
    public void failToUpdateNickname() {
        User user = User.builder()
            .email("test123@test.com")
            .nickname("17171771")
            .nicknameModifiedDate(LocalDateTime.now().minusDays(8))
            .build();
        String email = "test123@test.com";
        UserDto.SaveRequest requestDto = UserDto.SaveRequest.builder()
            .nickname("자우림")
            .build();
        String nicknameAfter = requestDto.getNickname();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);

        userService.updateNickname(email, requestDto);

        assertThat(user.getNickname()).isEqualTo(requestDto.getNickname());
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 닉네임 중복으로 닉네임 변경에 실패한다.")
    public void failToUpdateNicknameByDuplicate() {
        User user = createUserDto().toEntity();
        String email = "test123@test.com";
        UserDto.SaveRequest requestDto = UserDto.SaveRequest.builder()
            .nickname("자우림")
            .build();
        String nicknameAfter = requestDto.getNickname();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(true);

        assertThrows(DuplicateNicknameException.class,
            () -> userService.updateNickname(email, requestDto));
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);
    }

    @Test
    @DisplayName("닉네인 변경 실패 - 닉네임을 변경하고 7일이 지나지 않았다면 닉네임 변경에 실패한다.")
    public void failToUpdateNicknameByTerm() {
        User user = createUserDto().toEntity();
        UserDto.SaveRequest requestDto = UserDto.SaveRequest.builder()
            .nickname("자우림")
            .build();
        String email = "test123@test.com";
        String nicknameAfter = requestDto.getNickname();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);

        assertThrows(UnableToChangeNicknameException.class,
            () -> userService.updateNickname(email, requestDto));
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);
    }

    @Test
    @DisplayName("비밀번호가 일치하여 회원 탈퇴 성공한다.")
    public void deleteSuccess() {
        User user = createUser();
        String email = user.getEmail();
        String password = user.getPassword();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userRepository.existsByEmailAndPassword(any(), any())).willReturn(true);
        given(tradeService.hasUsersProgressingTrade(user)).willReturn(false);

        userService.delete(email, password);

        verify(userRepository, atLeastOnce()).deleteByEmail(email);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않아 회원 탈퇴 실패한다.")
    public void failToWithdrawalIfMismatchingPassword() {
        User user = createUser();
        String email = user.getEmail();
        String password = user.getPassword();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userRepository.existsByEmailAndPassword(any(), any())).willReturn(false);

        assertThrows(WrongPasswordException.class, () -> userService.delete(email, password));

        verify(userRepository, never()).deleteByEmail(email);
    }

    @Test
    @DisplayName("잔여 포인트가 존재하여 회원 탈퇴에 실패한다.")
    public void failToWithdrawalIfHasRemainingPoints() {
        User user = createUser();
        String email = user.getEmail();
        String password = user.getPassword();
        user.chargingPoint(999999L);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userRepository.existsByEmailAndPassword(any(), any())).willReturn(true);
        given(tradeService.hasUsersProgressingTrade(user)).willReturn(false);

        assertThrows(HasRemainingPointException.class, () -> userService.delete(email, password));
        verify(userRepository, never()).deleteByEmail(email);
    }

    @Test
    @DisplayName("진행중인 거래가 존재하여 회원 탈퇴에 실패한다.")
    public void failToWithdrawalIfHasProgressingTrade() {
        User user = createUser();
        String email = user.getEmail();
        String password = user.getPassword();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userRepository.existsByEmailAndPassword(any(), any())).willReturn(true);
        given(tradeService.hasUsersProgressingTrade(user)).willReturn(true);

        assertThrows(HasProgressingTradeException.class, () -> userService.delete(email, password));
        verify(userRepository, never()).deleteByEmail(email);
    }
}