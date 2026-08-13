package com.doktorthe2nd.min.net;

import java.util.HashMap;
import java.util.Map;

/**
All protocol operation codes.
Naming follows the server-side convention.
Use {@link #name(int)} to get a human-readable label for logging.
*/
public final class OpcodeTable {
    private OpcodeTable() {}

    // ── Session ────────────────────────────────────────────────────────
    public static final int ping = 1;                 // Пинг
    public static final int debug = 2;                // Отладка / понг
    public static final int reconnect = 3;            // Реконнект
    public static final int log = 5;                  // Аналитика / события
    public static final int sessionInit = 6;          // Инициализация сессии (хэндшейк)
    public static final int contactsGet = 8;          // Синхронизация списка контактов

    // ── Profile ────────────────────────────────────────────────────────
    public static final int profile = 16;             // Обновление профиля

    // ── Auth ───────────────────────────────────────────────────────────
    public static final int authRequest = 17;         // Запрос OTP-кода
    public static final int auth = 18;                // Проверка OTP-кода
    public static final int login = 19;               // Вход (загрузка чатов/контактов)
    public static final int logout = 20;              // Выход из аккаунта
    public static final int sync = 21;                // Синхронизация данных
    public static final int config = 22;              // Настройки приватности / конфиг
    public static final int authConfirm = 23;         // Завершение регистрации

    // ── Auth 2FA ───────────────────────────────────────────────────────
    public static final int authLoginRestorePassword = 101;   // Восстановление пароля
    public static final int auth2faDetails = 104;              // Детали 2FA
    public static final int externalCallback = 105;            // Внешний коллбэк
    public static final int authValidatePassword = 107;        // Валидация пароля
    public static final int authValidateHint = 108;            // Валидация подсказки пароля
    public static final int authVerifyEmail = 109;             // Верификация email
    public static final int authCheckEmail = 110;              // Проверка email
    public static final int authSet2fa = 111;                  // Установка 2FA
    public static final int authCreateTrack = 112;             // Создание трека авторизации
    public static final int authCheckPassword = 113;           // Проверка пароля при регистрации
    public static final int authLoginCheckPassword = 115;      // Проверка пароля при входе
    public static final int authLoginProfileDelete = 116;      // Удаление профиля при входе

    // ── Assets (стикеры) ───────────────────────────────────────────────
    public static final int presetAvatars = 25;                // Пресетные аватарки
    public static final int assetsGet = 26;                    // Получение стикерпаков
    public static final int assetsUpdate = 27;                 // Синхронизация стикеров
    public static final int assetsGetByIds = 28;               // Стикерпаки по ID
    public static final int assetsAdd = 29;                    // Добавление стикерпака
    public static final int assetsRemove = 259;                // Удаление стикерпака
    public static final int assetsMove = 260;                  // Перемещение стикерпака
    public static final int assetsListModify = 261;            // Изменение списка стикерпаков

    // ── Contacts ───────────────────────────────────────────────────────
    public static final int contactInfo = 32;                  // Информация о контакте
    public static final int contactAdd = 33;                   // Добавление контакта
    public static final int contactUpdate = 34;                // Обновление контакта (блок и т.д.)
    public static final int contactPresence = 35;              // Запрос присутствия по ID
    public static final int contactList = 36;                  // Список заблокированных
    public static final int contactSearch = 37;                // Поиск контакта
    public static final int contactMutual = 38;                // Общие контакты
    public static final int contactPhotos = 39;                // Фото контакта
    public static final int contactSort = 40;                  // Сортировка контактов
    public static final int contactVerify = 42;                // Верификация контакта
    public static final int removeContactPhoto = 43;           // Удаление фото контакта
    public static final int contactInfoByPhone = 46;           // Поиск контакта по номеру

    // ── Chats ──────────────────────────────────────────────────────────
    public static final int chatInfo = 48;                     // Информация о чате / создание группы
    public static final int chatHistory = 49;                  // История сообщений
    public static final int chatMark = 50;                     // Отметка прочитанным
    public static final int chatMedia = 51;                    // Медиа чата
    public static final int chatDelete = 52;                   // Удаление чата
    public static final int chatsList = 53;                    // Список чатов
    public static final int chatClear = 54;                    // Очистка истории чата
    public static final int chatUpdate = 55;                   // Обновление настроек чата
    public static final int chatCheckLink = 56;                // Проверка ссылки чата
    public static final int chatJoin = 57;                     // Вступление в группу / канал
    public static final int chatLeave = 58;                    // Выход из чата
    public static final int chatMembers = 59;                  // Участники группы
    public static final int publicSearch = 60;                 // Глобальный поиск
    public static final int chatPersonalConfig = 61;           // Персональные настройки чата
    public static final int chatCreate = 63;                   // Создание чата

    // ── Messages ───────────────────────────────────────────────────────
    public static final int msgSend = 64;                      // Отправка сообщения
    public static final int msgTyping = 65;                    // Индикатор набора текста
    public static final int msgDelete = 66;                    // Удаление сообщения
    public static final int msgEdit = 67;                      // Редактирование сообщения
    public static final int chatSearch = 68;                   // Поиск по чату
    public static final int msgSharePreview = 70;              // Превью ссылки
    public static final int msgGet = 71;                       // Получение сообщения по ID
    public static final int msgSearchTouch = 72;               // Точечный поиск сообщений
    public static final int msgSearch = 73;                    // Поиск сообщений
    public static final int msgGetStat = 74;                   // Статистика сообщения
    public static final int chatSubscribe = 75;                // Подписка на обновления чата
    public static final int msgDeleteRange = 92;               // Удаление диапазона сообщений

    // ── Reactions ──────────────────────────────────────────────────────
    public static final int msgReaction = 178;                         // Отправка реакции
    public static final int msgCancelReaction = 179;                   // Отмена реакции
    public static final int msgGetReactions = 180;                     // Получение реакций
    public static final int msgGetDetailedReactions = 181;             // Детальные реакции
    public static final int chatReactionsSettingsSet = 257;            // Установка настроек реакций
    public static final int reactionsSettingsGetByChatId = 258;        // Настройки реакций чата

    // ── Calls & Video ──────────────────────────────────────────────────
    public static final int videoChatStart = 76;                       // Начало видеочата
    public static final int chatMembersUpdate = 77;                    // Обновление участников / добавление
    public static final int videoChatStartActive = 78;                 // Инициация активного звонка
    public static final int videoChatHistory = 79;                    // История звонков
    public static final int videoChatDeleteHistory = 164;             // Удаление записей истории звонков
    public static final int videoChatCreateJoinLink = 84;             // Ссылка для входа в видеочат
    public static final int videoChatJoinByLink = 166;                // Вход в звонок по ссылке
    public static final int videoChatMembers = 195;                   // Участники видеочата
    public static final int getInboundCalls = 103;                    // Входящие звонки

    // ── Media & Files ──────────────────────────────────────────────────
    public static final int photoUpload = 80;                         // Загрузка фото
    public static final int stickerUpload = 81;                       // Загрузка стикера
    public static final int videoUpload = 82;                         // Загрузка видео
    public static final int videoPlay = 83;                           // URL видео
    public static final int chatPinSetVisibility = 86;                // Видимость закрепов
    public static final int fileUpload = 87;                          // Загрузка файла
    public static final int fileDownload = 88;                        // Скачивание файла
    public static final int linkInfo = 89;                            // Информация по ссылке / вход в канал
    public static final int audioPlay = 301;                          // Воспроизведение аудио

    // ── Sessions ───────────────────────────────────────────────────────
    public static final int sessionsInfo = 96;                        // Запрос активных сессий
    public static final int sessionsClose = 97;                       // Закрытие всех сессий
    public static final int phoneBindRequest = 98;                    // Запрос привязки телефона
    public static final int phoneBindConfirm = 99;                    // Подтверждение привязки телефона

    // ── Bots ───────────────────────────────────────────────────────────
    public static final int chatComplain = 117;                       // Жалоба на чат
    public static final int msgSendCallback = 118;                    // Коллбэк бота
    public static final int suspendBot = 119;                         // Приостановка бота
    public static final int chatBotCommands = 144;                    // Команды бота
    public static final int botInfo = 145;                            // Информация о боте

    // ── Location ───────────────────────────────────────────────────────
    public static final int locationStop = 124;                       // Остановка трансляции геолокации

    // ── Mentions ───────────────────────────────────────────────────────
    public static final int getLastMentions = 127;                    // Последние упоминания

    // ── Stickers (creation) ────────────────────────────────────────────
    public static final int stickerCreate = 193;                      // Создание стикера
    public static final int stickerSuggest = 194;                     // Предложение стикера

    // ── Notifications (push от сервера) ────────────────────────────────
    public static final int notifMessage = 128;                       // Новое сообщение
    public static final int notifTyping = 129;                        // Индикатор набора
    public static final int notifMark = 130;                          // Прочитано
    public static final int notifContact = 131;                       // Обновление контакта
    public static final int notifPresence = 132;                      // Статус онлайн
    public static final int notifConfig = 134;                        // Обновление конфига
    public static final int notifChat = 135;                          // Обновление чата
    public static final int notifAttach = 136;                        // Загрузка файла завершена
    public static final int notifCallStart = 137;                     // Входящий звонок
    public static final int notifContactSort = 139;                   // Пересортировка контактов
    public static final int notifMsgDeleteRange = 140;                // Удаление диапазона
    public static final int notifMsgDelete = 142;                     // Удаление сообщения
    public static final int notifCallbackAnswer = 143;                // Ответ бота
    public static final int notifLocation = 147;                      // Обновление геолокации
    public static final int notifLocationRequest = 148;               // Запрос геолокации
    public static final int notifAssetsUpdate = 150;                  // Обновление стикеров
    public static final int notifDraft = 152;                         // Черновик
    public static final int notifDraftDiscard = 153;                  // Сброс черновика
    public static final int notifMsgDelayed = 154;                    // Отложенное сообщение
    public static final int notifMsgReactionsChanged = 155;           // Изменение реакций
    public static final int notifMsgYouReacted = 156;                 // Ваша реакция
    public static final int notifProfile = 159;                       // Обновление профиля
    public static final int notifBanners = 292;                       // Баннеры
    public static final int notifFolders = 277;                       // Обновление папок

    // ── Transcription ───────────────────────────────────────────────────
    public static final int audioTranscription = 202;                 // Запрос транскрибации аудио
    public static final int transcriptionResult = 293;                // Результат транскрибации (push)

    // ── Misc ───────────────────────────────────────────────────────────
    public static final int okToken = 158;                            // OK-токен
    public static final int webAppInitData = 160;                     // Данные WebApp
    public static final int complain = 161;                           // Жалоба
    public static final int complainReasonsGet = 162;                 // Причины жалобы
    public static final int draftSave = 176;                          // Сохранение черновика
    public static final int draftDiscard = 177;                       // Удаление черновика
    public static final int chatHide = 196;                           // Скрытие чата
    public static final int chatSearchCommonParticipants = 198;       // Общие участники
    public static final int profileDelete = 199;                      // Удаление профиля
    public static final int profileDeleteTime = 200;                  // Таймер удаления профиля
    public static final int authQrApprove = 290;                      // Подтверждение QR-входа
    public static final int chatSuggest = 300;                        // Предложения чатов

    // ── Polls ──────────────────────────────────────────────────────────
    public static final int sendVote = 304;                           // Голосование
    public static final int votersListByAnswer = 305;                 // Список голосовавших
    public static final int getPollUpdates = 306;                     // Обновления опроса

    // ── Folders ────────────────────────────────────────────────────────
    public static final int foldersGet = 272;                         // Получение папок
    public static final int foldersGetById = 273;                     // Папка по ID
    public static final int foldersUpdate = 274;                      // Обновление / создание папки
    public static final int foldersReorder = 275;                     // Сортировка папок
    public static final int foldersDelete = 276;                      // Удаление папки

    // ── Stories ────────────────────────────────────────────────────────
    public static final int storiesList = 208;                        // Лента историй (кольца-превью)
    public static final int storiesListByOwner = 209;                 // Превью по списку владельцев
    public static final int storiesGetByOwner = 210;                  // Полные истории владельцев
    public static final int storiesGetStats = 211;                    // Агрегированная статистика
    public static final int storiesGetDetailedStats = 212;            // Детальная статистика
    public static final int storiesReact = 213;                       // Реакция на историю
    public static final int storiesMark = 214;                        // Отметка просмотренной
    public static final int storiesSend = 215;                        // Публикация истории
    public static final int notifStoriesUpdate = 216;                 // Обновление кольца (push)
    public static final int storiesEdit = 217;                        // Изменение настроек истории
    public static final int storiesDelete = 218;                      // Удаление историй
    public static final int storiesGetByStoryId = 220;                // Истории по ID

    // ── Name mapping ──────────────────────────────────────────────────

    private static final Map<Integer, String> NAMES = new HashMap<>();

    static {
        NAMES.put(ping, "PING");
        NAMES.put(debug, "DEBUG");
        NAMES.put(reconnect, "RECONNECT");
        NAMES.put(log, "LOG");
        NAMES.put(sessionInit, "SESSION_INIT");
        NAMES.put(contactsGet, "CONTACTS_GET");

        NAMES.put(profile, "PROFILE");

        NAMES.put(authRequest, "AUTH_REQUEST");
        NAMES.put(auth, "AUTH");
        NAMES.put(login, "LOGIN");
        NAMES.put(logout, "LOGOUT");
        NAMES.put(sync, "SYNC");
        NAMES.put(config, "CONFIG");
        NAMES.put(authConfirm, "AUTH_CONFIRM");

        NAMES.put(authLoginRestorePassword, "AUTH_LOGIN_RESTORE_PASSWORD");
        NAMES.put(auth2faDetails, "AUTH_2FA_DETAILS");
        NAMES.put(externalCallback, "EXTERNAL_CALLBACK");
        NAMES.put(authValidatePassword, "AUTH_VALIDATE_PASSWORD");
        NAMES.put(authValidateHint, "AUTH_VALIDATE_HINT");
        NAMES.put(authVerifyEmail, "AUTH_VERIFY_EMAIL");
        NAMES.put(authCheckEmail, "AUTH_CHECK_EMAIL");
        NAMES.put(authSet2fa, "AUTH_SET_2FA");
        NAMES.put(authCreateTrack, "AUTH_CREATE_TRACK");
        NAMES.put(authCheckPassword, "AUTH_CHECK_PASSWORD");
        NAMES.put(authLoginCheckPassword, "AUTH_LOGIN_CHECK_PASSWORD");
        NAMES.put(authLoginProfileDelete, "AUTH_LOGIN_PROFILE_DELETE");

        NAMES.put(presetAvatars, "PRESET_AVATARS");
        NAMES.put(assetsGet, "ASSETS_GET");
        NAMES.put(assetsUpdate, "ASSETS_UPDATE");
        NAMES.put(assetsGetByIds, "ASSETS_GET_BY_IDS");
        NAMES.put(assetsAdd, "ASSETS_ADD");
        NAMES.put(assetsRemove, "ASSETS_REMOVE");
        NAMES.put(assetsMove, "ASSETS_MOVE");
        NAMES.put(assetsListModify, "ASSETS_LIST_MODIFY");

        NAMES.put(contactInfo, "CONTACT_INFO");
        NAMES.put(contactAdd, "CONTACT_ADD");
        NAMES.put(contactUpdate, "CONTACT_UPDATE");
        NAMES.put(contactPresence, "CONTACT_PRESENCE");
        NAMES.put(contactList, "CONTACT_LIST");
        NAMES.put(contactSearch, "CONTACT_SEARCH");
        NAMES.put(contactMutual, "CONTACT_MUTUAL");
        NAMES.put(contactPhotos, "CONTACT_PHOTOS");
        NAMES.put(contactSort, "CONTACT_SORT");
        NAMES.put(contactVerify, "CONTACT_VERIFY");
        NAMES.put(removeContactPhoto, "REMOVE_CONTACT_PHOTO");
        NAMES.put(contactInfoByPhone, "CONTACT_INFO_BY_PHONE");

        NAMES.put(chatInfo, "CHAT_INFO");
        NAMES.put(chatHistory, "CHAT_HISTORY");
        NAMES.put(chatMark, "CHAT_MARK");
        NAMES.put(chatMedia, "CHAT_MEDIA");
        NAMES.put(chatDelete, "CHAT_DELETE");
        NAMES.put(chatsList, "CHATS_LIST");
        NAMES.put(chatClear, "CHAT_CLEAR");
        NAMES.put(chatUpdate, "CHAT_UPDATE");
        NAMES.put(chatCheckLink, "CHAT_CHECK_LINK");
        NAMES.put(chatJoin, "CHAT_JOIN");
        NAMES.put(chatLeave, "CHAT_LEAVE");
        NAMES.put(chatMembers, "CHAT_MEMBERS");
        NAMES.put(publicSearch, "PUBLIC_SEARCH");
        NAMES.put(chatPersonalConfig, "CHAT_PERSONAL_CONFIG");
        NAMES.put(chatCreate, "CHAT_CREATE");

        NAMES.put(msgSend, "MSG_SEND");
        NAMES.put(msgTyping, "MSG_TYPING");
        NAMES.put(msgDelete, "MSG_DELETE");
        NAMES.put(msgEdit, "MSG_EDIT");
        NAMES.put(chatSearch, "CHAT_SEARCH");
        NAMES.put(msgSharePreview, "MSG_SHARE_PREVIEW");
        NAMES.put(msgGet, "MSG_GET");
        NAMES.put(msgSearchTouch, "MSG_SEARCH_TOUCH");
        NAMES.put(msgSearch, "MSG_SEARCH");
        NAMES.put(msgGetStat, "MSG_GET_STAT");
        NAMES.put(chatSubscribe, "CHAT_SUBSCRIBE");
        NAMES.put(msgDeleteRange, "MSG_DELETE_RANGE");

        NAMES.put(msgReaction, "MSG_REACTION");
        NAMES.put(msgCancelReaction, "MSG_CANCEL_REACTION");
        NAMES.put(msgGetReactions, "MSG_GET_REACTIONS");
        NAMES.put(msgGetDetailedReactions, "MSG_GET_DETAILED_REACTIONS");
        NAMES.put(chatReactionsSettingsSet, "CHAT_REACTIONS_SETTINGS_SET");
        NAMES.put(reactionsSettingsGetByChatId, "REACTIONS_SETTINGS_GET_BY_CHAT_ID");

        NAMES.put(videoChatStart, "VIDEO_CHAT_START");
        NAMES.put(chatMembersUpdate, "CHAT_MEMBERS_UPDATE");
        NAMES.put(videoChatStartActive, "VIDEO_CHAT_START_ACTIVE");
        NAMES.put(videoChatHistory, "VIDEO_CHAT_HISTORY");
        NAMES.put(videoChatDeleteHistory, "VIDEO_CHAT_DELETE_HISTORY");
        NAMES.put(videoChatCreateJoinLink, "VIDEO_CHAT_CREATE_JOIN_LINK");
        NAMES.put(videoChatJoinByLink, "VIDEO_CHAT_JOIN_BY_LINK");
        NAMES.put(videoChatMembers, "VIDEO_CHAT_MEMBERS");
        NAMES.put(getInboundCalls, "GET_INBOUND_CALLS");

        NAMES.put(photoUpload, "PHOTO_UPLOAD");
        NAMES.put(stickerUpload, "STICKER_UPLOAD");
        NAMES.put(videoUpload, "VIDEO_UPLOAD");
        NAMES.put(videoPlay, "VIDEO_PLAY");
        NAMES.put(chatPinSetVisibility, "CHAT_PIN_SET_VISIBILITY");
        NAMES.put(fileUpload, "FILE_UPLOAD");
        NAMES.put(fileDownload, "FILE_DOWNLOAD");
        NAMES.put(linkInfo, "LINK_INFO");
        NAMES.put(audioPlay, "AUDIO_PLAY");

        NAMES.put(sessionsInfo, "SESSIONS_INFO");
        NAMES.put(sessionsClose, "SESSIONS_CLOSE");
        NAMES.put(phoneBindRequest, "PHONE_BIND_REQUEST");
        NAMES.put(phoneBindConfirm, "PHONE_BIND_CONFIRM");

        NAMES.put(chatComplain, "CHAT_COMPLAIN");
        NAMES.put(msgSendCallback, "MSG_SEND_CALLBACK");
        NAMES.put(suspendBot, "SUSPEND_BOT");
        NAMES.put(chatBotCommands, "CHAT_BOT_COMMANDS");
        NAMES.put(botInfo, "BOT_INFO");

        NAMES.put(locationStop, "LOCATION_STOP");

        NAMES.put(getLastMentions, "GET_LAST_MENTIONS");

        NAMES.put(stickerCreate, "STICKER_CREATE");
        NAMES.put(stickerSuggest, "STICKER_SUGGEST");

        NAMES.put(notifMessage, "NOTIF_MESSAGE");
        NAMES.put(notifTyping, "NOTIF_TYPING");
        NAMES.put(notifMark, "NOTIF_MARK");
        NAMES.put(notifContact, "NOTIF_CONTACT");
        NAMES.put(notifPresence, "NOTIF_PRESENCE");
        NAMES.put(notifConfig, "NOTIF_CONFIG");
        NAMES.put(notifChat, "NOTIF_CHAT");
        NAMES.put(notifAttach, "NOTIF_ATTACH");
        NAMES.put(notifCallStart, "NOTIF_CALL_START");
        NAMES.put(notifContactSort, "NOTIF_CONTACT_SORT");
        NAMES.put(notifMsgDeleteRange, "NOTIF_MSG_DELETE_RANGE");
        NAMES.put(notifMsgDelete, "NOTIF_MSG_DELETE");
        NAMES.put(notifCallbackAnswer, "NOTIF_CALLBACK_ANSWER");
        NAMES.put(notifLocation, "NOTIF_LOCATION");
        NAMES.put(notifLocationRequest, "NOTIF_LOCATION_REQUEST");
        NAMES.put(notifAssetsUpdate, "NOTIF_ASSETS_UPDATE");
        NAMES.put(notifDraft, "NOTIF_DRAFT");
        NAMES.put(notifDraftDiscard, "NOTIF_DRAFT_DISCARD");
        NAMES.put(notifMsgDelayed, "NOTIF_MSG_DELAYED");
        NAMES.put(notifMsgReactionsChanged, "NOTIF_MSG_REACTIONS_CHANGED");
        NAMES.put(notifMsgYouReacted, "NOTIF_MSG_YOU_REACTED");
        NAMES.put(notifProfile, "NOTIF_PROFILE");
        NAMES.put(notifBanners, "NOTIF_BANNERS");
        NAMES.put(notifFolders, "NOTIF_FOLDERS");

        NAMES.put(audioTranscription, "AUDIO_TRANSCRIPTION");
        NAMES.put(transcriptionResult, "TRANSCRIPTION_RESULT");

        NAMES.put(okToken, "OK_TOKEN");
        NAMES.put(webAppInitData, "WEB_APP_INIT_DATA");
        NAMES.put(complain, "COMPLAIN");
        NAMES.put(complainReasonsGet, "COMPLAIN_REASONS_GET");
        NAMES.put(draftSave, "DRAFT_SAVE");
        NAMES.put(draftDiscard, "DRAFT_DISCARD");
        NAMES.put(chatHide, "CHAT_HIDE");
        NAMES.put(chatSearchCommonParticipants, "CHAT_SEARCH_COMMON_PARTICIPANTS");
        NAMES.put(profileDelete, "PROFILE_DELETE");
        NAMES.put(profileDeleteTime, "PROFILE_DELETE_TIME");
        NAMES.put(authQrApprove, "AUTH_QR_APPROVE");
        NAMES.put(chatSuggest, "CHAT_SUGGEST");

        NAMES.put(sendVote, "SEND_VOTE");
        NAMES.put(votersListByAnswer, "VOTERS_LIST_BY_ANSWER");
        NAMES.put(getPollUpdates, "GET_POLL_UPDATES");

        NAMES.put(foldersGet, "FOLDERS_GET");
        NAMES.put(foldersGetById, "FOLDERS_GET_BY_ID");
        NAMES.put(foldersUpdate, "FOLDERS_UPDATE");
        NAMES.put(foldersReorder, "FOLDERS_REORDER");
        NAMES.put(foldersDelete, "FOLDERS_DELETE");

        NAMES.put(storiesList, "STORIES_LIST");
        NAMES.put(storiesListByOwner, "STORIES_LIST_BY_OWNER_ID");
        NAMES.put(storiesGetByOwner, "STORIES_GET_BY_OWNER_ID");
        NAMES.put(storiesGetStats, "STORIES_GET_STATS");
        NAMES.put(storiesGetDetailedStats, "STORIES_GET_DETAILED_STATS");
        NAMES.put(storiesReact, "STORIES_REACT");
        NAMES.put(storiesMark, "STORIES_MARK");
        NAMES.put(storiesSend, "STORIES_SEND");
        NAMES.put(notifStoriesUpdate, "NOTIF_STORIES_UPDATE");
        NAMES.put(storiesEdit, "STORIES_EDIT");
        NAMES.put(storiesDelete, "STORIES_DELETE");
        NAMES.put(storiesGetByStoryId, "STORIES_GET_BY_STORY_ID");
    }

    /**
     * Returns a human-readable name for the given opcode.
     *
     * @param opcode the operation code
     * @return the name, or {@code "UNKNOWN(opcode)"} if not found
     */
    public static String name(int opcode) {
        return NAMES.getOrDefault(opcode, "UNKNOWN(" + opcode + ")");
    }
}