package com.oceanview.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dto.BillDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/customer-history")
public class CustomerHistoryServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                    if (localDate == null) jsonWriter.nullValue();
                    else jsonWriter.value(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
                @Override
                public LocalDate read(JsonReader jsonReader) throws IOException {
                    return LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            }).create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Unauthorized access. Please log in.");
            out.print(gson.toJson(jsonResponse));
            out.flush();
            return;
        }

        int userId = (int) session.getAttribute("userId");
        List<BillDTO> reservations = reservationDAO.getReservationsByUserId(userId);

        jsonResponse.put("status", "success");
        jsonResponse.put("reservations", reservations);

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}
