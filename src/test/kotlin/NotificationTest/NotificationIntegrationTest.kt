import com.rentmycar.entities.Notification
import com.rentmycar.repositories.InMemoryNotificationRepository
import com.rentmycar.services.TimeSlotNotificationService
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeSlotNotificationServiceIntegrationTest {

    private val notificationRepository = InMemoryNotificationRepository()
    private val timeSlotNotificationService = TimeSlotNotificationService(notificationRepository)

    @Test
    fun testUpdateTimeSlotCreatesNotification() {
        val timeSlotId = 1L
        val userId = 1L
        val newStartTime = java.time.LocalDateTime.now().plusDays(1)
        val newEndTime = java.time.LocalDateTime.now().plusDays(2)

        timeSlotNotificationService.updateTimeSlot(timeSlotId, userId, newStartTime, newEndTime)

        val notifications = notificationRepository.getNotificationsByUserId(userId)
        assertEquals(1, notifications.size)
        assertEquals("Your timeslot has been updated.", notifications[0].message)
    }

    @Test
    fun testDeleteTimeSlotCreatesNotification() {
        val timeSlotId = 1L
        val userId = 1L

        timeSlotNotificationService.deleteTimeSlot(timeSlotId, userId)

        val notifications = notificationRepository.getNotificationsByUserId(userId)
        assertEquals(1, notifications.size)
        assertEquals("Your timeslot has been deleted.", notifications[0].message)
    }
}