package com.quality.server;

import com.quality.db.dao.*;
import com.quality.model.*;
import com.quality.network.Request;
import com.quality.network.Response;
import com.quality.service.AnalyticsService;
import com.quality.service.InspectionService;
import com.quality.util.PasswordUtil;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // DAO
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final ProductCategoryDAO categoryDAO = new ProductCategoryDAO();
    private final QualityStandardDAO standardDAO = new QualityStandardDAO();
    private final QualityCriteriaDAO criteriaDAO = new QualityCriteriaDAO();
    private final StandardCriteriaDAO standardCriteriaDAO = new StandardCriteriaDAO();
    private final InspectionDAO inspectionDAO = new InspectionDAO();
    private final DefectTypeDAO defectTypeDAO = new DefectTypeDAO();
    private final DefectDAO defectDAO = new DefectDAO();

    // Сервисы (бизнес-логика)
    private final InspectionService inspectionService = new InspectionService();
    private final AnalyticsService analyticsService = new AnalyticsService();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("[Клиент подключён] " + socket.getInetAddress());

            while (true) {
                Request request = (Request) in.readObject();
                System.out.println("[Запрос] " + request.getAction());

                Response response = handleRequest(request);
                out.writeObject(response);
                out.flush();
                out.reset();
            }

        } catch (EOFException e) {
            System.out.println("[Клиент отключился] " + socket.getInetAddress());
        } catch (Exception e) {
            System.err.println("[Ошибка] " + e.getMessage());
        } finally {
            close();
        }
    }

    @SuppressWarnings("unchecked")
    private Response handleRequest(Request request) {
        try {
            String action = request.getAction();
            Object data = request.getData();

            switch (action) {

                // =================== АВТОРИЗАЦИЯ ===================

                case "LOGIN": {
                    String[] creds = (String[]) data;
                    String hash = PasswordUtil.hash(creds[1]);
                    User user = userDAO.authenticate(creds[0], hash);
                    if (user != null) {
                        return Response.ok("Авторизация успешна", user);
                    }
                    return Response.error("Неверный логин или пароль");
                }

                case "REGISTER": {
                    User newUser = (User) data;
                    newUser.setPasswordHash(PasswordUtil.hash(newUser.getPasswordHash()));
                    newUser.setActive(true);
                    userDAO.create(newUser);
                    return Response.ok("Регистрация успешна", newUser);
                }

                // =================== ПОЛЬЗОВАТЕЛИ ===================

                case "GET_ALL_USERS":
                    return Response.ok(userDAO.findAll());

                case "UPDATE_USER": {
                    User user = (User) data;
                    userDAO.update(user);
                    return Response.ok("Пользователь обновлён", null);
                }

                case "TOGGLE_USER_ACTIVE": {
                    int userId = (int) data;
                    userDAO.toggleActive(userId);
                    return Response.ok("Статус изменён", null);
                }

                case "GET_ROLES":
                    return Response.ok(roleDAO.findAll());

                // =================== КАТЕГОРИИ ===================

                case "GET_CATEGORIES":
                    return Response.ok(categoryDAO.findAll());

                case "CREATE_CATEGORY": {
                    ProductCategory cat = (ProductCategory) data;
                    categoryDAO.create(cat);
                    return Response.ok("Категория создана", cat);
                }

                case "UPDATE_CATEGORY": {
                    ProductCategory cat = (ProductCategory) data;
                    categoryDAO.update(cat);
                    return Response.ok("Категория обновлена", null);
                }

                case "DELETE_CATEGORY": {
                    categoryDAO.delete((int) data);
                    return Response.ok("Категория удалена", null);
                }

                // =================== ПРОДУКЦИЯ ===================

                case "GET_PRODUCTS":
                    return Response.ok(productDAO.findAll());

                case "SEARCH_PRODUCTS":
                    return Response.ok(productDAO.search((String) data));

                case "GET_PRODUCTS_BY_CATEGORY":
                    return Response.ok(productDAO.findByCategory((int) data));

                case "CREATE_PRODUCT": {
                    Product p = (Product) data;
                    productDAO.create(p);
                    return Response.ok("Продукт создан", p);
                }

                case "UPDATE_PRODUCT": {
                    Product p = (Product) data;
                    productDAO.update(p);
                    return Response.ok("Продукт обновлён", null);
                }

                case "DELETE_PRODUCT": {
                    productDAO.delete((int) data);
                    return Response.ok("Продукт удалён", null);
                }

                // =================== СТАНДАРТЫ ===================

                case "GET_STANDARDS":
                    return Response.ok(standardDAO.findAll());

                case "GET_STANDARDS_BY_CATEGORY":
                    return Response.ok(standardDAO.findByCategory((int) data));

                case "CREATE_STANDARD": {
                    QualityStandard s = (QualityStandard) data;
                    standardDAO.create(s);
                    return Response.ok("Стандарт создан", s);
                }

                case "UPDATE_STANDARD": {
                    QualityStandard s = (QualityStandard) data;
                    standardDAO.update(s);
                    return Response.ok("Стандарт обновлён", null);
                }

                case "DELETE_STANDARD": {
                    standardDAO.delete((int) data);
                    return Response.ok("Стандарт удалён", null);
                }

                // =================== КРИТЕРИИ ===================

                case "GET_CRITERIA":
                    return Response.ok(criteriaDAO.findAll());

                case "CREATE_CRITERIA": {
                    QualityCriteria c = (QualityCriteria) data;
                    criteriaDAO.create(c);
                    return Response.ok("Критерий создан", c);
                }

                case "GET_STANDARD_CRITERIA":
                    return Response.ok(standardCriteriaDAO.findByStandard((int) data));

                case "ADD_STANDARD_CRITERIA": {
                    StandardCriteria sc = (StandardCriteria) data;
                    standardCriteriaDAO.addCriterionToStandard(sc);
                    return Response.ok("Критерий добавлен к стандарту", null);
                }

                case "REMOVE_STANDARD_CRITERIA": {
                    int[] ids = (int[]) data;
                    standardCriteriaDAO.removeCriterionFromStandard(ids[0], ids[1]);
                    return Response.ok("Критерий удалён из стандарта", null);
                }

                // =================== ТИПЫ ДЕФЕКТОВ ===================

                case "GET_DEFECT_TYPES":
                    return Response.ok(defectTypeDAO.findAll());

                case "CREATE_DEFECT_TYPE": {
                    DefectType dt = (DefectType) data;
                    defectTypeDAO.create(dt);
                    return Response.ok("Тип дефекта создан", dt);
                }

                // =================== ИНСПЕКЦИИ (бизнес-логика) ===================

                case "CONDUCT_INSPECTION": {
                    Object[] params = (Object[]) data;
                    Inspection inspection = (Inspection) params[0];
                    List<InspectionResult> results = (List<InspectionResult>) params[1];
                    Inspection result = inspectionService.conductInspection(inspection, results);
                    return Response.ok("Инспекция проведена. Статус: " + result.getStatus(), result);
                }

                case "GET_ALL_INSPECTIONS":
                    return Response.ok(inspectionDAO.findAll());

                case "GET_MY_INSPECTIONS":
                    return Response.ok(inspectionDAO.findByInspector((int) data));

                case "GET_INSPECTION_DETAILS":
                    return Response.ok(inspectionService.getInspectionDetails((int) data));

                case "GET_PRODUCT_HISTORY":
                    return Response.ok(inspectionService.getProductHistory((int) data));

                case "GET_REPORT_BY_PERIOD": {
                    String[] dates = (String[]) data;
                    return Response.ok(inspectionService.getReportByPeriod(dates[0], dates[1]));
                }

                case "ADD_DEFECT": {
                    Defect defect = (Defect) data;
                    inspectionService.addDefect(defect);
                    return Response.ok("Дефект зафиксирован", defect);
                }

                case "GET_DEFECTS_BY_INSPECTION":
                    return Response.ok(defectDAO.findByInspection((int) data));

                // =================== АНАЛИТИКА ===================

                case "GET_PRODUCT_RATINGS":
                    return Response.ok(analyticsService.getProductRatings());

                case "GET_DEFECT_STATISTICS":
                    return Response.ok(analyticsService.getDefectStatistics());

                case "GET_QUALITY_TREND":
                    return Response.ok(analyticsService.getQualityTrend((int) data));

                case "GET_CATEGORY_COMPARISON":
                    return Response.ok(analyticsService.getCategoryComparison());

                case "GET_DEFECTS_BY_PRODUCT":
                    return Response.ok(analyticsService.getDefectsByProduct((int) data));

                // =================== СЛУЖЕБНЫЕ ===================

                case "PING":
                    return Response.ok("PONG", null);
                case "DELETE_CRITERIA": {
                    criteriaDAO.delete((int) data);
                    return Response.ok("Критерий удалён", null);
                }

                case "DELETE_DEFECT_TYPE": {
                    defectTypeDAO.delete((int) data);
                    return Response.ok("Тип дефекта удалён", null);
                }
                default:
                    return Response.error("Неизвестная команда: " + action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Ошибка сервера: " + e.getMessage());
        }
    }

    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}