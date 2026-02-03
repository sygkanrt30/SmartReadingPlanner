package ru.yanin.practice.user_service.service.user.reading_profile;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yanin.practice.user_service.exception.EntityNotFoundException;
import ru.yanin.practice.user_service.model.dto.request.user.reading_profle.UpdateReadingGoalsRequest;
import ru.yanin.practice.user_service.model.entity.ReadingProfile;
import ru.yanin.practice.user_service.model.mapper.ReadingProfileMapperImpl;
import ru.yanin.practice.user_service.repository.ReadingProfileRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.yanin.shared.genre.Genre.*;

@ExtendWith(MockitoExtension.class)
class ReadingProfileServiceImplTest {

    @Mock
    private ReadingProfileRepository readingProfileRepository;

    private ReadingProfileServiceImpl readingProfileServiceImpl;

    @BeforeEach
    void setUp() {
        var readingProfileMapper = new ReadingProfileMapperImpl();
        readingProfileServiceImpl = new ReadingProfileServiceImpl(readingProfileRepository, readingProfileMapper);
    }

    @Test
    void updateReadingGoals_ShouldUpdateReadingProfile_WhenRequestChangeReadingProfileState() {
        var currentReadingProfile = getCurrentReadingProfile();
        var request = Instancio.of(UpdateReadingGoalsRequest.class)
                .set(field(UpdateReadingGoalsRequest::wordsPerMin), 250)
                .set(field(UpdateReadingGoalsRequest::minPerDay), 120)
                .set(field(UpdateReadingGoalsRequest::bookPerMonth), 2)
                .create();
        when(readingProfileRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(currentReadingProfile));

        readingProfileServiceImpl.updateReadingGoals(1L, request);

        ArgumentCaptor<ReadingProfile> profileCaptor = ArgumentCaptor.forClass(ReadingProfile.class);
        verify(readingProfileRepository).save(profileCaptor.capture());
        ReadingProfile savedProfile = profileCaptor.getValue();
        assertEquals(120, savedProfile.minPerDay());
        assertEquals(2, savedProfile.bookPerMonth());
        assertEquals(250, savedProfile.wordsPerMin());
    }

    private ReadingProfile getCurrentReadingProfile() {
        return Instancio.of(ReadingProfile.class)
                .set(field(ReadingProfile::wordsPerMin), 250)
                .set(field(ReadingProfile::minPerDay), 123)
                .set(field(ReadingProfile::bookPerMonth), 3)
                .set(field(ReadingProfile::preferredGenres), new HashSet<>(List.of(ADVENTURE, ROMANCE, SCIENCE_FICTION)))
                .create();
    }

    @Test
    void updateReadingGoals_ShouldNotUpdateReadingProfile_WhenRequestNotChangeReadingProfileState() {
        var currentReadingProfile = getCurrentReadingProfile();
        var request = Instancio.of(UpdateReadingGoalsRequest.class)
                .set(field(UpdateReadingGoalsRequest::wordsPerMin), 250)
                .set(field(UpdateReadingGoalsRequest::minPerDay), 123)
                .set(field(UpdateReadingGoalsRequest::bookPerMonth), 3)
                .create();
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(currentReadingProfile));

        readingProfileServiceImpl.updateReadingGoals(anyLong(), request);

        verify(readingProfileRepository, never()).save(any());
    }

    @Test
    void updateReadingGoals_ShouldThrowException_WhenReadingProfileNotFound() {
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> readingProfileServiceImpl.updateReadingGoals(15L, mock(UpdateReadingGoalsRequest.class)));
        verify(readingProfileRepository, never()).save(any());
    }

    @Test
    void addGenre_ShouldAddNewGenres_WhenNewRecipesSentInRequest() {
        var currentReadingProfile = getCurrentReadingProfile();
        var stringGenres = Set.of("SCIENCE_FICTION", "DRAMA", "NOIR");
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(currentReadingProfile));

        readingProfileServiceImpl.addGenres(stringGenres, 1L);

        verify(readingProfileRepository).save(currentReadingProfile);
        assertThat(currentReadingProfile.preferredGenres())
                .hasSize(5)
                .containsExactlyInAnyOrder(
                        ADVENTURE,
                        ROMANCE,
                        SCIENCE_FICTION,
                        DRAMA,
                        NOIR
                );
    }

    @Test
    void addGenre_ShouldNotAddNewGenres_WhenNewRecipesNotSentInRequest() {
        var currentReadingProfile = getCurrentReadingProfile();
        var stringGenres = Set.of("SCIENCE_FICTION", "ROMANCE", "ADVENTURE");
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(currentReadingProfile));

        readingProfileServiceImpl.addGenres(stringGenres, 3L);

        verify(readingProfileRepository).save(currentReadingProfile);
        assertThat(currentReadingProfile.preferredGenres())
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        ADVENTURE,
                        SCIENCE_FICTION,
                        ROMANCE
                );
    }

    @Test
    void addGenre_ShouldNotThrowException_WhenRecipesInDifferentCase() {
        var currentReadingProfile = getCurrentReadingProfile();
        var stringGenres = Set.of("SCIeNCE_FICTION", "RoMANcE", "adventure");
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(currentReadingProfile));

        assertDoesNotThrow(() -> readingProfileServiceImpl.addGenres(stringGenres, 3L));
    }

    @Test
    void addGenre_ShouldThrowException_WhenRecipesInvalid() {
        var currentReadingProfile = getCurrentReadingProfile();
        var stringGenres = Set.of("SCIeNCE FICTION", "RoMAcE", "adenture");
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(currentReadingProfile));

        assertThrows(IllegalArgumentException.class, () -> readingProfileServiceImpl.addGenres(stringGenres, 3L));
    }

    @Test
    void addGenre_ShouldAddPartNewGenres_WhenPartNewRecipesValidAndOtherPartInvalid() {
        var currentReadingProfile = getCurrentReadingProfile();
        var stringGenres = Set.of("SCIENCE_FICTION", "Drama", "COMEDI");
        when(readingProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(currentReadingProfile));

        readingProfileServiceImpl.addGenres(stringGenres, 3L);

        verify(readingProfileRepository).save(currentReadingProfile);
        assertThat(currentReadingProfile.preferredGenres())
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        ADVENTURE,
                        SCIENCE_FICTION,
                        ROMANCE,
                        DRAMA
                );
    }
}
