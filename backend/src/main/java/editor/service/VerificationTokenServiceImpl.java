package editor.service;

import editor.entity.User;
import editor.entity.VerificationToken;
import editor.repository.UserRepository;
import editor.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken =
                new VerificationToken(user,token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            return "invalid token";
        }else{
            User user = verificationToken.getUser();
            Calendar calendar = Calendar.getInstance();
            if((verificationToken.getExpirationTime().getTime()
               - calendar.getTime().getTime()) <= 0){
                return "expired";
            }
            user.setEnabled(true);
            userRepository.save(user);
            return "valid token";
        }
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
}
