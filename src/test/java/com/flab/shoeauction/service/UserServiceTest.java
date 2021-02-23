package com.flab.shoeauction.service;

import static com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.AddressBookDto;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.addressBook.AddressBookRepository;
import com.flab.shoeauction.domain.user.Account;
import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.domain.user.repository.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    AddressBookRepository addressBookRepository;
    @InjectMocks
    UserService userService;

    private SaveRequest createUserDto() {
        SaveRequest saveRequest = SaveRequest.builder()
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();
        return saveRequest;
    }


    @Test
    @DisplayName("이메일과 닉네임이 중복되지 않으면 회원가입에 성공한다.")
    public void signUp_Successful() {

        SaveRequest saveRequest = createUserDto();

        when(userRepository.existsByEmail("test123@test.com")).thenReturn(false);
        when(userRepository.existsByNickname("17171771")).thenReturn(false);

        userService.save(saveRequest);

        verify(userRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입에 실패한다.")
    public void emailDuplicate() {
        SaveRequest saveRequest = createUserDto();
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.save(saveRequest));

        verify(userRepository, atLeastOnce()).existsByEmail("test123@test.com");
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입에 실패한다.")
    public void nicknameDuplicate() {
        SaveRequest saveRequest = createUserDto();
        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () -> userService.save(saveRequest));

        verify(userRepository, atLeastOnce()).existsByNickname("17171771");
    }

    @Test
    @DisplayName("비밀번호 찾기 성공 - 전달받은 객체(이메일)가 회원이라면 비밀번호 변경에 성공한다.")
    public void updatePasswordByForget() {
        ChangePasswordRequest changePasswordRequest = of("test123.test.com", "test12345", null);
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
            .of(null, "test12345", "test1234");
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
            .of(null, "test12345", "test1234");
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
        Address address = new Address("우리집", "땡땡땡로 123", "123동 456호", "12345");
        AddressBook addressBook = new AddressBook(address);
        AddressBookDto addressBookDto =new AddressBookDto(2L, "친구집", "사랑로 123", "123-1", "11111");
        when(addressBookRepository.findById(addressBookDto.getId()))
            .thenReturn(Optional.of(addressBook));

        userService.updateAddressBook(addressBookDto);

        assertThat(addressBook.getAddress().getAddressName())
            .isEqualTo(addressBookDto.getAddressName());
        assertThat(addressBook.getAddress().getDetailedAddress())
            .isEqualTo(addressBookDto.getDetailedAddress());
        assertThat(addressBook.getAddress().getPostalCode())
            .isEqualTo(addressBookDto.getPostalCode());
        assertThat(addressBook.getAddress().getRoadNameAddress())
            .isEqualTo(addressBookDto.getRoadNameAddress());

        verify(addressBookRepository, atLeastOnce()).findById(any());
    }

    @Test
    @DisplayName("주소록 추가 - 올바른 주소 입력시 주소록 추가에 성공한다.")
    public void addAddressBook() {
        ArrayList<AddressBook> addressBooks = new ArrayList<>();
        User user = User.builder()
            .email("test123@test.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01020180103")
            .addressesBook(addressBooks).build();
        String email = "test123@test.com";
        Address address = new Address("우리집", "땡땡땡로 123", "111동 111호", "12345");
        Address address2 = new Address("친구집", "친구집로 123", "222동 222호", "67890");
        Address address3 = new Address("별장", "해변로 123", "333동 333호", "11111");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.addAddressBook(email, address);
        userService.addAddressBook(email, address2);
        userService.addAddressBook(email, address3);

        assertThat(user.getAddressesBook().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("주소록 조회 - 해당 USER의 주소록을 불러온다.")
    public void getAddressBooks() {
        ArrayList<AddressBook> addressBooks = new ArrayList<>();
        User user = User.builder()
            .email("test123@test.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01020180103")
            .addressesBook(addressBooks).build();
        String email = "test123@test.com";
        Address address = new Address("우리집", "땡땡땡로 123", "111동 111호", "12345");
        Address address2 = new Address("친구집", "친구집로 123", "222동 222호", "67890");
        Address address3 = new Address("별장", "해변로 123", "333동 333호", "11111");
        user.addAddressBook(address);
        user.addAddressBook(address2);
        user.addAddressBook(address3);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<AddressBook> addressBookList = userService.getAddressBooks(email);

        assertThat(addressBookList.size()).isEqualTo(3);
        verify(userRepository, atLeastOnce()).findByEmail(email);
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
        SaveRequest requestDto = SaveRequest.builder()
            .nickname("자우림")
            .build();
        String nicknameAfter = requestDto.getNickname();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);

        userService.updateNickname(email,requestDto);

        assertThat(user.getNickname()).isEqualTo(requestDto.getNickname());
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 닉네임 중복으로 닉네임 변경에 실패한다.")
    public void failToUpdateNicknameByDuplicate() {
        User user = createUserDto().toEntity();
        String email = "test123@test.com";
        SaveRequest requestDto = SaveRequest.builder()
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
        User user =createUserDto().toEntity();
        SaveRequest requestDto = SaveRequest.builder()
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

    @DisplayName("비밀번호가 일치하여 회원 탈퇴 성공한다.")
    @Test
    public void deleteSuccess() {
        SaveRequest saveRequest = createUserDto();
        String email = saveRequest.getEmail();
        String password = saveRequest.getPassword();

        when(userRepository.existsByEmailAndPassword(email, encryptionService.encrypt(password)))
            .thenReturn(true);

        userService.delete(email, password);

        verify(userRepository, atLeastOnce()).deleteByEmail(email);
    }

    @DisplayName("비밀번호가 일치하지 않아 회원 탈퇴 실패한다.")
    @Test
    public void deleteFailure() {
        SaveRequest saveRequest = createUserDto();
        String email = saveRequest.getEmail();
        String password = saveRequest.getPassword();

        when(userRepository.existsByEmailAndPassword(email, encryptionService.encrypt(password)))
            .thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> userService.delete(email, password));

        verify(userRepository, never()).deleteByEmail(email);
    }
}