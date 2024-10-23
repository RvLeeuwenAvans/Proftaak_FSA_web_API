import com.rentmycar.BaseTest
import com.rentmycar.authentication.PasswordHasher
import com.rentmycar.entities.*
import com.rentmycar.repositories.NotificationRepository
import com.rentmycar.services.ReservationService
import com.rentmycar.services.TimeSlotService
import com.rentmycar.utils.Category
import com.rentmycar.utils.FuelType
import com.rentmycar.utils.Transmission
import com.rentmycar.utils.UserRole
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationIntegrationTest : BaseTest() {

    private val notificationRepository = NotificationRepository()
    private val timeSlotNotificationService = TimeSlotService()
    private lateinit var testUser: User
    private lateinit var testCar: Car
    private lateinit var testTimeslot: Timeslot

    @BeforeTest
    fun setupUser() {
        val hashedPassword = PasswordHasher.hashPassword("password123")
        testUser = transaction {
            User.new {
                firstName = "John"
                lastName = "Doe"
                username = "johndoe"
                email = "johndoe@example.com"
                password = hashedPassword
                role = UserRole.DEFAULT
            }
        }
    }

    @BeforeTest
    fun setupCar() {
        testCar = transaction {
            Car.new {
                owner = testUser
                model = Model.find { Models.name eq "Model 1" }.first()
                licensePlate = "xx-123-X"
                year = 2020
                color = "red"
                price = 10.00
                transmission = Transmission.AUTOMATIC
                fuel = FuelType.GAS
                category = Category.ICE
            }
        }
    }

    @BeforeTest
    fun setupTimeSlot() {
        testTimeslot = transaction {
            Timeslot.new {
                car = testCar
                availableFrom = LocalDateTime.now()
                    .plusMinutes(10) // now plus ten minutes, because you cannot edit a current timeslot
                availableUntil = LocalDateTime.now().plusHours(1)
            }
        }
    }

    @Test
    fun testSuccessfulUpdateTimeSlotNotification() = withTestApplication {
        val timeSlotId = testTimeslot.toDTO().id

        ReservationService().createReservation(testUser, testCar.toDTO().id)

        timeSlotNotificationService.updateTimeSlot(
            testUser,
            timeSlotId,
            LocalDateTime.now().plusHours(1).toKotlinLocalDateTime(),
            LocalDateTime.now().plusHours(2).toKotlinLocalDateTime()
        )

        val notifications = notificationRepository.getNotifications(testUser.id.value)

        assertEquals(1, notifications.size)
        assertEquals("Time slot updated", notifications[0].subject)
    }

    @Test
    fun testSuccessDeleteTimeSlotNotification() {
        val timeSlotId = testTimeslot.toDTO().id

        ReservationService().createReservation(testUser, testCar.toDTO().id)
        timeSlotNotificationService.deleteTimeSlot(testUser, timeSlotId)

        val notifications = notificationRepository.getNotifications(testUser.id.value)

        assertEquals(1, notifications.size)
        assertEquals("Time slot removed", notifications[0].subject)
    }
}