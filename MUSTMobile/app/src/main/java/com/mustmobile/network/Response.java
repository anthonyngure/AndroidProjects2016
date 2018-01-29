package com.mustmobile.network;

/**
 * Created by Tosh on 10/8/2015.
 */
public  abstract class Response {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String DATA = "data";
    public static final String UPDATES_COUNT = "updates_count";
    public static final String LAST_UPDATE_ID = "last_update_id";
    public static final String LAST_ID = "last_id";

    public static final class Error{
        public static final String WRONG_PASSWORD = "wrong_password";
        public static final String USER_NOT_IN_SYSTEM = "user_not_in_system";
    }

    public static final class UserData {
        public static final String USER_NUMBER = "user_number";
        public static final String PASSWORD = "password";
        public static final String NAME = "name";
        public static final String CLASS = "class";
        public static final String REGISTRATION_NUMBER = "registration_number";
        public static final String PRIVILEGE_CODE = "privilege_code";
        public static String USER_DATA = "user_data";
    }

    public static final class PastPaperData{
        public static final String NAME = "name";
        public static final String URL = "url";
    }

    public static final class BookData {
        public static final String ID = "book_id";
        public static final String AUTHOR = "author";
        public static final String COVER = "cover";
        public static final String URL = "url";
        public static final String TITLE = "title";
    }

    public static final class UpdateData {
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String CREATED_AT = "created_at";
        public static final String TIMES_READ = "times_read";
        public static final String ID = "id";
    }

    public static final class ProgramData {
        public static final String NAME = "name";
        public static final String CODE = "code";
        public static final String REQUIREMENTS = "requirements";
        public static final String DURATION = "duration";
        public static final String MODE = "mode";
        public static final String FEES = "fees";
        public static final String CAMPUS = "campus";
    }

    public static final class DownloadData {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String TIME_UPLOADED = "time_uploaded";
        public static final String TIMES_DOWNLOADED = "times_downloaded";
        public static final String URL = "url";
    }

    public static final class TimetableData {
        public static final String DAY = "day";
        public static final String HOUR_ONE = "hour_one";
        public static final String HOUR_TWO = "hour_two";
        public static final String HOUR_THREE = "hour_three";
        public static final String HOUR_FOUR = "hour_four";
        public static final String HOUR_FIVE = "hour_five";
        public static final String HOUR_SIX = "hour_six";
        public static final String HOUR_SEVEN = "hour_seven";
        public static final String HOUR_EIGHT = "hour_eight";
        public static final String HOUR_NINE = "hour_nine";
        public static final String HOUR_TEN = "hour_ten";
        public static final String CLASS = "class";
    }

    public static final class RentalData {
        public static final String NAME = "name";
        public static final String LOCATION = "location";
        public static final String CONTACT = "contact";
        public static final String DISTANCE = "distance";
        public static final String PRICE = "price";
        public static final String URL = "url";
    }

    public static final class UnionOfficialData {
        public static final String NAME = "name";
        public static final String POST = "post";
        public static final String MESSAGE = "message";
        public static final String MESSAGE_TITLE = "message_title";
        public static final String PROFILE_URL = "profile_url";
    }

    public static final class EClassVideoData {
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String DESCRIPTION = "description";
        public static final String URL = "url";
        public static final String UPLOAD_TIME = "upload_time";
        public static final String VIEWS = "views";
        public static final String THUMBNAIL = "thumbnail";
    }

    public static final class SemesterData {
        public static final String REPORTING_NEW_STUDENTS = "reporting_new_students";
        public static final String REPORTING_CONTINUING_STUDENTS = "reporting_continuing_students";
        public static final String COMMENCEMENT_OF_LECTURES = "commencement_of_lectures";
        public static final String CAT_ONE_START = "cat_one_start";
        public static final String CAT_ONE_END = "cat_one_end";
        public static final String CAT_TWO_START = "cat_two_start";
        public static final String CAT_TWO_END = "cat_two_end";
        public static final String END_OF_LECTURES = "end_of_lectures";
        public static final String EXAMINATIONS_START = "examinations_start";
        public static final String EXAMINATIONS_END = "examinations_end";
        public static final String TEACHING_WEEKS = "teaching_weeks";
        public static final String EXAMINATION_WEEKS = "examination_weeks";
        public static final String BREAK_START = "break_start";
        public static final String BREAK_END = "break_end";
        public static final String BREAK_WEEKS = "break_weeks";
        public static final String REMARKS = "remarks";
    }

    public static final class FeeStructureData {
        public static final String TUITION_SEM_ONE = "tuition_sem_one";
        public static final String EXAMINATION_SEM_ONE = "examination_sem_one";
        public static final String MEDICAL_SEM_ONE = "medical_sem_one";
        public static final String ACTIVITY_SEM_ONE = "activity_sem_one";
        public static final String INTERNET_SEM_ONE = "internet_sem_one";
        public static final String TRIP_SEM_ONE = "trip_sem_one";
        public static final String LIBRARY_SEM_ONE = "library_sem_one";

        public static final String STUDENT_UNION = "student_union";
        public static final String ATTACHMENT = "attachment";
        public static final String ACCOMODATION = "accomodation";
        public static final String REGISTRATION_FEE = "registration_fee";
        public static final String TOTAL_PAYABLE_SEM_ONE = "total_payable_sem_one";
        public static final String TOTAL_PAYABLE_SEM_TWO = "total_payable_sem_two";

        public static final String TUITION_SEM_TWO = "tuition_sem_two";
        public static final String EXAMINATION_SEM_TWO = "examination_sem_two";
        public static final String MEDICAL_SEM_TWO = "medical_sem_two";
        public static final String ACTIVITY_SEM_TWO = "activity_sem_two";
        public static final String INTERNET_SEM_TWO = "internet_sem_two";
        public static final String TRIP_SEM_TWO = "trip_sem_two";
        public static final String LIBRARY_SEM_TWO = "library_sem_two";
    }

    public static final class ForumData {
        public static final String NAME = "name";
        public static final String CODE = "code";
        public static final String DESCRIPTION = "description";
        public static final String ICON_URL = "icon_url";
    }

    public static final class TopicData {
        public static final String CONTENT = "content";
        public static final String ID = "id";
        public static final String FORUM_CODE = "forum_code";
        public static final String AUTHOR_NUMBER = "author_number";
        public static final String TIME = "time";
        public static final String TAG = "tag";
        public static final String VIEWS = "views";
        public static final String ANSWERS = "answers";
        public static final String AUTHOR_NAME = "author_name";
    }

    public static final class ExchangeData {
        public static final String CONTENT = "content";
        public static final String TOPIC_ID = "topic_id";
        public static final String AUTHOR_NUMBER = "author_number";
        public static final String TIME = "time";
        public static final String AUTHOR_PROFILE = "author_profile";
        public static final String AUTHOR_NAME = "author_name";
        public static final String EXCHANGE_ID = "id";
    }

    public static class VersionControlData {
        public static final String VERSION_CODE = "version_code";
        public static final String VERSION_NAME = "version_name";
        public static final String MESSAGE = "message";
        public static final String DOWNLOAD_LINK = "download_link";
        public static final String RELEASE_DATE = "release_date";
    }

    public static class GalleryData {
        public static final String URL = "url";
        public static final String DESCRIPTION = "description";
    }
}
